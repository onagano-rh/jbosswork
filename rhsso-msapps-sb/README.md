- [概要](#概要)
- [RH-SSOの起動とレルムの設定](#rh-ssoの起動とレルムの設定)
- [api-backendの動作確認](#api-backendの動作確認)
- [ui-frontendの動作確認](#ui-frontendの動作確認)

# 概要

本サンプルプロジェクトは以下の三つのサーバーで構成される。

| サーバー    | ポート番号 | 役割                          | 備考                                                             |
|-------------|------------|-------------------------------|------------------------------------------------------------------|
| RH-SSO      | 28080      | 認証認可サービス              | KeycloakのDockerコンテナで代用する。                             |
| ui-frontend | 8080       | ブラウザから利用するWebアプリ | spring-boot-starter-oauth2-clientコンポーネントを使う。          |
| api-backend | 8180       | REST APIを提供するサーバー    | spring-boot-starter-oauth2-resource-serverコンポーネントを使う。 |

ui-frontendはRH-SSOにより保護され、認証済みでない場合はRH-SSOのログイン画面にリダイレクトされる。
認証に成功したら、RH-SSOから取得したアクセストークンを使ってapi-backendのREST APIを呼び出し、その結果を利用してブラウザにレスポンスを返す。

api-backendもRH-SSOにより保護され、アクセストークンを持たないリクエストには400番台のレスポンスコードを返す。ui-frontendから呼び出されるが、適切なアクセストークンさえ付けていればcurlコマンドによる直接アクセスも可能である。

api-backendは以下のREST APIを公開する。

- /public
  - 認証なしで誰でもアクセスできる
- /protected
  - 認証しないと (アクセストークンがなければ) アクセスできない
- /protected/admin
  - 認証だけでなく、認証されたユーザーがadminロールを持っていないと (適切に認可されていないと) アクセスできない

レスポンスの内容はいずれも固定の単純なJSONオブジェクトなので省略する。

ui-frontendは、本来であればHTMLを返却するWeb UIを提供すべきであるが、簡便のためこれもJSONオブジェクトを返すだけにしている。api-backendと同様のリクエストパスと保護の構成をしており、アクセスを受けたパスと同じリクエストをapi-backendに対して行ない、得られたレスポンスをJSONオブジェクトの一部にしてブラウザに返却する。

- /public
  - 認証なしで誰でもアクセスできる
  - api-backendの/publicを呼び出し、その結果をレスポンスに含めて返却する
- /protected
  - 認証しないと (アクセストークンがなければ) アクセスできない
  - api-backendの/protectedを呼び出し、その結果をレスポンスに含めて返却する
- /protected/admin
  - 認証だけでなく、認証されたユーザーがadminロールを持っていないと (適切に認可されていないと) アクセスできない
  - api-backendの/protected/adminを呼び出し、その結果をレスポンスに含めて返却する

認証認可サービスの利用ガイドにあるサンプル (rhsso-workshop-sb) は、RH-SSOのログイン画面へのリダイレクトという、ブラウザとのインタラクションを伴うという意味ではui-frontendと同様のものだったが、ui-frontendではさらにログインの結果として得られたアクセストークンを用いて別のサーバーにアクセスするという、より現実的な例になっている。

ui-frontendとapi-backendは、リクエストパスやアクセス保護の構成はほとんど同じであるにもかかわらず、OIDCクライアントとしては扱いが大きく異なることに注意する。RH-SSOのクライアント設定項目のAccess Typeで言えば、ui-frontendはconfidentialクライアント、api-backendはbearer-onlyクライアントとなり設定がそもそも異なるほか、使用するライブラリ (Spring Bootのコンポーネント) にも違いが出てくる。これは、ui-frontendは (通常のWebアプリがするように) Cookieヘッダーにより認証を行なうのに対し、api-backendはAuthorizationヘッダーをまず見るというHTTPのレイヤーでの違いに起因する。

# RH-SSOの起動とレルムの設定

RH-SSOをKeycloakのDockerイメージで代用する。"http://localhost:28080/" でアクセスできるように以下の設定で起動する (podmanコマンドでも可)。

```shell
docker run --name keycloak-legacy -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password -p 28080:8080 quay.io/keycloak/keycloak:18.0.2-legacy -c standalone.xml -b 0.0.0.0
```

利用するイメージのタグが "18.0.2-legacy" となっているように、RH-SSO 7.6と最もバージョンが近いものを選択している。なお、最新のKeycloak ("legacy"が付かないQuarkusベースのKeycloak) でも動作するが、最新のKeycloakはリクエストパスに"/auth"が付かないことに注意する (最新版を使うなら以下"/auth"が含まれていればその部分を削除する)。

起動したコンテナを停止する場合は `docker stop keycloak-legacy`、再起動する場合は `docker start keycloak-legacy` を実行する。

ブラウザで http://localhost:28080/auth にアクセスし、Keycloakの管理コンソールに admin/password でログインして以下の設定を行なう。

- レルム "rhssotest" の作成
- ロール "admin" の作成
- ユーザ "testuser01" の作成
  - パスワードを "password" に設定
  - ロール "admin" の付与
- ユーザ "testuser02" の作成
  - パスワードを "password" に設定
- IDトークンにロールのクレイムが含まれるように設定
  - "Client Scopes > roles > Mappers > realm roles" にて "Add ID token: ON" に設定
- Admin CLIクライアントが "admin" ロールを認識できるように設定
  - "Clients > admin-cli > Scope" にて "admin" を "Assigned Roles" に移動

ロールはレルムのレベルで設定することも、各クライアントのレベルで設定することもできるが、ここでは (画面左の"Roles"を選択して) レルムのレベルで行なう。adminロールをtestuser01にのみ付与し、ロールによる認可の違いをテストできるようにする。

IDトークンにロールのクレイムを含めるのは、ui-frontendが認証認可に (アクセストークンではなく) IDトークンを参照するためである。OIDCの仕様でscopeクレイムは定義されているがロールはKeycloakの独自クレイムであるため、ロールの情報を認可のために利用するにはIDトークンに含める必要がある。

Admin CLIは、Keycloakがデフォルトで用意しているクライアントであり、デフォルトで "Direct Access Grants Enabled: ON" の設定になっている。この設定が有効であれば、(ブラウザでの対話的なログイン画面でではなく) コマンドラインからでもユーザ名とパスワードと引き換えにアクセストークンを取得することができるようになる。これでcurlコマンドによる (バッチ処理による非対話的な) アクセストークンの取得が可能になり、任意のクライアントのテストに利用できる。ただしこうして取得したアクセストークンには全てのロールが載るわけではないので、上記の設定画面で特にadminロールが載るように設定し、api-backendのテストに用いる。

# api-backendの動作確認

以下のようにapi-backendを開発モードで起動する。

```
$ cd api-backend
$ mvn spring-boot:run
(...) Tomcat started on port(s): 8180 (http) with context path ''
```

api-backendでは、設定ファイル src/main/resources/application.yml の下記の部分でRH-SSOの特にrhssotestレルムで保護するように設定している。

```
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:28080/auth/realms/rhssotest
```

Keycloakで言うbearer-onlyクライアントに相当するものを、Spring Bootでは "Resource Server" と呼ぶ。このタイプのクライアントはアクセストークンを受け取り解釈するだけでログインに関する処理は必要ないため、設定は簡単になる。

RH-SSOにこのクライアントを登録する必要は必ずしもない。これは、トークンの検証には署名に用いた公開鍵のみが必要だが、それは `issuer-uri` の設定から取得できるためである (このURLに ".../.well-known/openid-configuration" を付けたパスにリクエストを送ると、公開鍵の取得先など様々な情報が得られる)。

Admin CLI (`client_id=admin-cli`) に適切なロールを載せる設定をしたので、ui-frontendがなくとも下記のようにcurlコマンドでアクセスできる。

```
$ TKN=$(curl -s 'http://localhost:28080/auth/realms/rhssotest/protocol/openid-connect/token' \
-d client_id=admin-cli \
-d username=testuser01 \
-d password=password \
-d grant_type=password | jq -r '.access_token')

$ curl -vH "Authorization: Bearer $TKN" 'http://localhost:8180/protected/admin'
(...)
< HTTP/1.1 200 OK
(...)
{"server":"api-backend","message":"protected admin"}
```

トークン取得に用いたtestuser01はadminロールを持っているため、/protected/adminにも適切にアクセスできている。

# ui-frontendの動作確認

ui-frontendはクライアント登録が必須であり、RH-SSOの管理コンソールで以下のように設定する。

- "Clients > Add Client" で下記のように設定
  Client ID: ui-frontend
  Client Protocol: openid-connect
  Root URL: http://localhost:8080
- "Settings" の画面で下記の設定を変更
  Access Type: confidential
- "Credentials" の画面で "Secret" の文字列を確認 (コピーして控えておく)

別のターミナルを開き、上記で控えたクライアントシークレット (confidentialクライアントに対して生成されるパスワード) を設定ファイルに反映させ、ui-frontendを起動する。

```
$ cd ui-frontend
$ vi src/main/resources/application.yml
(...)
spring:
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-id: ui-frontend
            client-secret: <控えたシークレットをペースト>
            authorization-grant-type: authorization_code
            scope: openid
        provider:
          keycloak:
            issuer-uri: http://localhost:28080/auth/realms/rhssotest
            user-name-attribute: preferred_username
(...)
$ mvn spring-boot:run
(...) Tomcat started on port(s): 8080 (http) with context path ''
```
 
このクライアントは通常のWebアプリなので、curlコマンドでは認証できずブラウザからアクセスする。例えば下記のURLにブラウザでアクセスすると、RH-SSOのログイン画面にリダイレクトされ、testuser01で認証すれば適切にレスポンスが表示される。

```
# http://localhost:8080/protected/admin

{
  "server": "ui-frontend",
  "message": "{\"server\":\"api-backend\",\"message\":\"protected area with admin role\"}"
}
```


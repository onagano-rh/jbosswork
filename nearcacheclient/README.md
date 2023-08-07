# Server setup

Disable auth and enable JBoss Marshalling in infinispan.xml:

```xml
<infinispan
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="urn:infinispan:config:14.0 https://infinispan.org/schemas/infinispan-config-14.0.xsd
                            urn:infinispan:server:14.0 https://infinispan.org/schemas/infinispan-server-14.0.xsd"
      xmlns="urn:infinispan:config:14.0"
      xmlns:server="urn:infinispan:server:14.0">

   <cache-container name="default" statistics="true">
      <transport cluster="${infinispan.cluster.name:cluster}" stack="${infinispan.cluster.stack:tcp}" node-name="${infinispan.node.name:}"/>
      <serialization marshaller="org.infinispan.jboss.marshalling.commons.GenericJBossMarshaller">
      </serialization>
   </cache-container>

   <server xmlns="urn:infinispan:server:14.0">
      <interfaces>
         <interface name="public">
            <inet-address value="${infinispan.bind.address:127.0.0.1}"/>
         </interface>
      </interfaces>

      <socket-bindings default-interface="public" port-offset="${infinispan.socket.binding.port-offset:0}">
         <socket-binding name="default" port="${infinispan.bind.port:11222}"/>
         <socket-binding name="memcached" port="11221"/>
      </socket-bindings>

      <security>
         <credential-stores>
            <credential-store name="credentials" path="credentials.pfx">
               <clear-text-credential clear-text="secret"/>
            </credential-store>
         </credential-stores>
         <security-realms>
            <security-realm name="default">
               <!-- Uncomment to enable TLS on the realm -->
               <!-- server-identities>
                  <ssl>
                     <keystore path="application.keystore"
                               password="password" alias="server"
                               generate-self-signed-certificate-host="localhost"/>
                  </ssl>
               </server-identities-->
               <properties-realm groups-attribute="Roles">
                  <user-properties path="users.properties"/>
                  <group-properties path="groups.properties"/>
               </properties-realm>
            </security-realm>
         </security-realms>
      </security>

      <endpoints socket-binding="default" />
   </server>
</infinispan>
```

Define "jbmCache":

```xml
<?xml version="1.0"?>
<infinispan xmlns="urn:infinispan:config:14.0">
    <cache-container>
        <caches>
            <distributed-cache name="jbmCache" owners="3" mode="SYNC" statistics="true">
                <encoding media-type="application/x-jboss-marshalling"/>
                <locking isolation="REPEATABLE_READ"/>
            </distributed-cache>
        </caches>
    </cache-container>
</infinispan>
```

# How to run the client

```sh
mvn clean compile exec:java -Dexec.mainClass=com.example.nearcachetest.Main
```


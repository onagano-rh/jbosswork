# Links:
# - https://fedoramagazine.org/keycloak-on-fedora-workstation-with-podman/
# - https://access.redhat.com/documentation/ja-jp/red_hat_enterprise_linux/8/html-single/deploying_different_types_of_servers/index#setting-apache-http-server_Deploying-different-types-of-servers

my-custom-ca.crt:
	openssl req \
	    -x509 \
	    -new \
	    -nodes \
	    -keyout private/my-custom-ca.key \
	    -sha256 \
	    -days 3650 \
	    -out my-custom-ca.crt \
	    -subj '/CN=My custom CA/DC=localhost'

# To view the certificate:
#     openssl x509 -text -noout -in my-custom-ca.crt
#
# To store the certificate:
#     sudo trust anchor --store my-custom-ca.crt
#     sudo update-ca-trust

private/myserver.net.csr:
	openssl req \
	    -new \
	    -newkey rsa:2048 \
	    -nodes \
	    -keyout private/myserver.net.key \
	    -sha256 \
	    -out private/myserver.net.csr \
	    -subj '/CN=myserver.net' \
	    -addext 'subjectAltName=DNS:myserver.net' \
	    -addext 'basicConstraints=critical,CA:FALSE' \
	    -addext 'keyUsage=digitalSignature' \
	    -addext 'extendedKeyUsage=serverAuth'

# To inspect the CSR:
#     openssl req -text -noout -in private/myserver.net.csr

myserver.net.crt:
	openssl x509 \
	    -req \
	    -days 365 \
	    -in private/myserver.net.csr \
	    -CA my-custom-ca.crt \
	    -CAkey private/my-custom-ca.key \
	    -out myserver.net.crt \
	    -set_serial 123401 \
	    -sha256 \
	    -copy_extensions copy

# To inspect the certificate:
#     openssl x509 -text -noout -in myserver.net.crt

deploy:
	cp private/myserver.net.key /etc/pki/tls/private/
	cp myserver.net.crt /etc/pki/tls/certs/
	cp my-custom-ca.crt /etc/pki/tls/certs/
	chown root:root /etc/pki/tls/private/myserver.net.key
	chmod 600 /etc/pki/tls/private/myserver.net.key
	cp ssl.conf /etc/httpd/conf.d/

# May need to open a port:
#     sudo firewall-cmd --permanent --add-port=443/tcp
#     sudo firewall-cmd --reload
#     sudo systemctl restart httpd


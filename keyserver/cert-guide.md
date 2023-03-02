openssl genrsa -out server-key.pem 4096
openssl req -new -sha256 -x509 -days 365 -keyout ca-key.pem -out ca-crt.pem -subj "/C=br/ST=sp/L=sp/O=irineu/OU=antunes/CN=192.168.226.92"

openssl req -new -sha256 -key server-key.pem -out ca-csr.pem -subj "/C=br/ST=sp/L=sp/O=irineu/OU=antunes/CN=192.168.226.92"
openssl x509 -req -days 365 -in ca-csr.pem -CA ca-crt.pem -CAkey ca-key.pem -CAcreateserial -out server-crt.pem

//4G

openssl genrsa -out server-key2.pem 4096
openssl req -new -sha256 -x509 -days 365 -keyout ca-key2.pem -out ca-crt2.pem -subj "/C=br/ST=sp/L=sp/O=irineu/OU=antunes/CN=192.168.1.92"

openssl req -new -sha256 -key server-key2.pem -out ca-csr2.pem -subj "/C=br/ST=sp/L=sp/O=irineu/OU=antunes/CN=192.168.1.92"
openssl x509 -req -days 365 -in ca-csr2.pem -CA ca-crt2.pem -CAkey ca-key2.pem -CAcreateserial -out server-crt2.pem
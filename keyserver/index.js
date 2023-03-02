import express from "express";
import http from "http";
import https from "https";
import bodyParser from "body-parser";
import crypto from "crypto"
import fs from "fs";

const app = express();

const privateKey  = fs.readFileSync('server-key.pem', 'utf8');
const certificate = fs.readFileSync('server-crt.pem', 'utf8');

const httpServer = http.Server(app);
const httpsServer = https.Server({key: privateKey, cert: certificate},app);

const genHash = (packet) => {
    return crypto.createHash('md5').update(JSON.stringify(packet).split("").reverse().join("")).digest("hex");
}

app.use(bodyParser.json())

app.get("/", (req,res) => {
    res.json("hello world");
});

app.post("/gentoken", (req,res) => {
    res.json(genHash(req.body));
});


app.post("/check", (req,res) => {
    
    let packet = {
        name: req.body.name,
        email: req.body.email
    };

    let hashKey = genHash(packet);

    let salt = new Buffer.from([0xFF, 0xEE, 0xAD, 0x66, 0xE0, 0xF9, 0x62, 0xAE], "hex");
    let key = crypto.pbkdf2Sync(hashKey, salt, 2000, 32, 'sha256');

    let iv = [];

    for(let i = 15; i >= 0; i--){
        iv.push(~key[i]);
    }

    let cipher = crypto.createCipheriv('aes-256-cbc', key, new Buffer.from(iv, "utf8"));
    let encrypted = cipher.update(JSON.stringify(packet));
    encrypted = Buffer.concat([encrypted, cipher.final()]);

    let serial = crypto.createHash('md5').update(encrypted).digest("hex");

    if(serial.toUpperCase() == req.body.key){
        res.json({
            status: "ok"
        });
    }else{
        res.status(400).json({
            status: "invalid"
        })
    }
   
});

httpsServer.listen(4000, function() {
    console.log('listening on *:4000');
});

httpServer.listen(3000, function() {
    console.log('listening on *:3000');
});
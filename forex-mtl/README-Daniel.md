Loaded SDK Amazon Coretto 20.0.2 due to the sbt version 1.8.0 requiring a version of JDK 20 or less, 
and I've used that one before and it worked well.

Basic query in the README is wrong. Actual working query to the OneFrame service is:
```shell
curl -H "token: 10dc303535874aeccc86a8251e6992f5" 'localhost:8080/rates?from=USD&to=JPY'
```
And actually, it doesn't require that token. I guess that requirement is part of the task? It's unclear.

Tasks:

OneFrame Service
1) Modify OneFrame service to limit requests to 1000 per day per token

OneFrameInterpreter
1) Build basic app ✓
2) Create client to connect to OneFrame service ✓
3) Cache requests to limit duplicate calls with lifetime of 5 minutes (basic version: hash map) ✓
4) Create docker images and docker-compose environment

Optional
1) Delay sending call to OneFrame Service and collect incoming requests (it doesn't provide deadline for responses)
2) Upgrade hash map to a fast-read database or something
3) Make PostMan suite
4) Improve error messages


Diary:
Created One Frame Interpreter service. Decided to make it a Scala 3 ZIO service for fun and to compare ZIO to Cats
Successfully sent a simple request from OFI to OF via ZIO HTTP Client
Add refined type for currency in OFI
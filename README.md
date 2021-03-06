# mocklog
HTTP Logger &amp; Mock Server

## Description

**mocklog** is a HTTP server that is for development purposes.

It helps for :
* Debugging HTTP calls 
* Mock server, to be able to send predetermined content. 

## Naming

The name was coined by [Markus Perndorfer](https://github.com/mpern) : 

> “mocklog” sounds kinda cute
> 
> Look at little "mocklog", playing with his files and serving http requests like the big boys!


## Usage

The easiest way is to use **Docker**:

    docker build -t mocklog .
    docker run --rm -v $PWD:/srv -p8080:8080 mocklog

This will server the current `incoming/` and `outgoing/` directory on the port `8080` of `localhost`.

Then each request body to the server will be dumped into `incoming/$PATH/$UUID` with `$PATH` being the path requested, and `$UUID` being the UUID of the request, which is autogenerated. Headers will be dumped into `incoming/$PATH/$UUID.$METHOD` with `$METHOD` being the HTTP Method used (GET, POST, PUT, ...). If the directories aren't present, they will be created on the fly.

Similarly, the replies are forged from the `outgoing/$PATH` file. Headers will be sourced from `outgoing/$PATH.$METHOD`. If the files don't exists, it is assumed to be empty, and nothing specific is sent. Note that some critical reply headers are always sent.

A specific HTTP header will always be added :

    X-Request-UUID: $UUID
    
It represents the UUID of the request, in order to be able to find its dump in the `incoming/` directory.

## Status

[![Java CI with Maven](https://github.com/steveschnepp/mocklog/actions/workflows/maven.yml/badge.svg)](https://github.com/steveschnepp/mocklog/actions/workflows/maven.yml)
[![CodeQL](https://github.com/steveschnepp/mocklog/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/steveschnepp/mocklog/actions/workflows/codeql-analysis.yml)

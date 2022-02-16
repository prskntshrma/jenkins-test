#!/bin/bash
repo="repo name"
newbranch="new branch name"
sourcebranch="source branch name"

org="azdo org name"
project="azdo project name"
pat="azdo pat with repo scope"
base64PAT=$(echo -n :$pat | base64)
echo $base64PAT

url="https://dev.azure.com/$org/$project/_apis/git/repositories/$repo/refs?filter=heads/$sourcebranch&api-version=6.0"
echo $url
objectId=$(curl $url -H "Authorization: Basic $base64PAT" | jq -r .value[0].objectId)
echo $objectId


url2="https://dev.azure.com/$org/$project/_apis/git/repositories/$repo/refs?api-version=5.1"
curl $url2 -d '[{"name": "refs/heads/'$newbranch'"'',"newObjectId": "'$objectId'", "oldObjectId": "0000000000000000000000000000000000000000"}]' -H "Authorization: Basic $base64PAT" -H "Content-Type: application/json"

pipeline {
    agent any
    parameters {
        string(name: 'REPO_NAME', description: 'url of git repo')
        string(name: 'SOURCE', description: 'source branch')
        string(name: 'TARGET', description: 'new branch')
        string(name: 'ORG', description: 'org name')
        string(name: 'PAT', description: 'personal access token')
        string(name: 'PROJECT', description: 'personal access token')
    }
    stages {
        stage("build") {
            steps {
                script {
                    sh '''
                        #!/bin/bash
                        sudo apt install -y jq
                        repo=$REPO_NAME
                        newbranch=$TARGET
                        sourcebranch=$SOURCE

                        org=$ORG
                        project=$PROJECT
                        pat=$PAT
                        base64PAT=$(echo -n :$pat | base64)
                        echo $base64PAT

                        url="https://dev.azure.com/$org/$project/_apis/git/repositories/$repo/refs?filter=heads/$sourcebranch&api-version=6.0"
                        echo $url
                        objectId=$(curl $url -H "Authorization: Basic $base64PAT" | jq -r .value[0].objectId)
                        echo $objectId


                        url2="https://dev.azure.com/$org/$project/_apis/git/repositories/$repo/refs?api-version=5.1"
                        curl $url2 -d '[{"name": "refs/heads/'$newbranch'"'',"newObjectId": "'$objectId'", "oldObjectId": "0000000000000000000000000000000000000000"}]' -H "Authorization: Basic $base64PAT" -H "Content-Type: application/json"

                    '''
                }
            }
        }
    }
}

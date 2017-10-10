

def call(Map PushConfig){ 

        withDockerRegistry(url:"${PushConfig.registryUrl}",credentialsId:"${PushConfig.registryCredId}"){
        script{
                sh script: """

                        docker tag ${PushConfig.localImageTag} ${PushConfig.pushImageTag}
                        docker push ${PushConfig.pushImageTag}

                    """, returnStdout: true
                }
        }

}



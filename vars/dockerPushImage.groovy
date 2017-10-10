

def call(Map PushConfig){ 

        withDockerRegistry(url:"${PushConfig.registryUrl}",credentialsId:"${PushConfig.registryCredId}"){
        script{ 
                sh script: """
                        docker tag ${PushConfig.localImageTag} ${PushConfig.registryUrl}/${PushConfig.pushImageTag}
                        docker push ${PushConfig.registryUrl}/${PushConfig.pushImageTag}
                    """, returnStdout: true
                }
        }

}



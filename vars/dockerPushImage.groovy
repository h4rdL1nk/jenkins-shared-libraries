

def call(Map PushConfig){ 

        withDockerRegistry(url:"${PushConfig.registryUrl}",credentialsId:"${PushConfig.registryCredId}"){
        script{ 
                def registryEndpoint = PushConfig.registryUrl.split('//')[1].trim()
                sh script: """
                        #!/bin/bash

                        set +x
                		
                        docker tag ${PushConfig.localImageTag} ${PushConfig.pushImageTag}
                        docker push ${registryEndpoint}/${PushConfig.pushImageTag}
                    """, returnStdout: true
                }
        }

}




def call(Map DeployConfig){
        withAWS(region:"${DeployConfig.awsRegion}",credentials:"${DeployConfig.awsCredId}"){
                script{
                        awsEnv = "${DeployConfig.awsAppEnv}"
                        awsAppName = "${DeployConfig.awsAppEnv}"
                        awsEcrImg = "${DeployConfig.awsAppEnv}"

                        sh script: """
                            #!/bin/bash
                            aws ecs list-clusters | jq -r '.clusterArns[]|select(test("^.*CL.*-${awsEnv}\$"))'
                            """, returnStdout: true
                }
        }
}

def call(Map DeployConfig){
        withAWS(region:"${DeployConfig.awsRegion}",credentials:"${DeployConfig.awsCredId}"){
                script{
                        awsEnv = "${DeployConfig.awsAppEnv}"
                        awsAppName = "${DeployConfig.awsAppName}"
                        awsEcrImg = "${DeployConfig.awsEcrImg}"

                        sh script: """
                                echo ENV: ${awsEnv}
                                echo APP: ${awsAppName}
                                echo IMG: ${awsEcrImg}
                                aws ecs list-clusters | jq -r '.clusterArns[]|select(test("^.*CL.*-'${env}'$"))'
                            """
                }
        }
}
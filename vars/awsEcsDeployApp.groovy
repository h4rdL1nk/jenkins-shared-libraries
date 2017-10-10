
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
                                aws ec2 list-clusters
                            """
                        sh script: """
                                aws ecs list-clusters
                            """, returnStdout: true
                }
        }
}

def call(Map DeployConfig){
        withAWS(region:"${DeployConfig.awsRegion}",credentials:"${DeployConfig.awsCredId}"){
                script{
                        awsEnv = "${DeployConfig.awsAppEnv}"
                        awsAppName = "${DeployConfig.awsAppEnv}"
                        awsEcrImg = "${DeployConfig.awsAppEnv}"

                        sh script: """
                                echo ENV: ${awsEnv}
                                echo APP: ${awsAppName}
                                echo IMG: ${awsEcrImg}
                                aws ec2 describe-instances
                            """
                        sh script: """
                                aws ecs list-clusters
                            """, returnStdout: true
                }
        }
}
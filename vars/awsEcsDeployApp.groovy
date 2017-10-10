
def call(Map DeployConfig){
        withAWS(region:"${DeployConfig.awsRegion}",credentials:"${DeployConfig.awsCredId}"){
                script{
                        awsEnv = "${DeployConfig.awsAppEnv}"
                        awsAppName = "${DeployConfig.awsAppEnv}"
                        awsEcrImg = "${DeployConfig.awsAppEnv}"

                        sh script: """
                                aws ecs list-clusters
                            """, returnStdout: true
                }
        }
}

def call(Map DeployConfig){
        withAWS(region:"${DeployConfig.awsRegion}",credentials:"${DeployConfig.awsCredId}"){
                script{
                        awsEnv = "${DeployConfig.awsAppEnv}"
                        awsAppName = "${DeployConfig.awsAppName}"
                        awsEcrImg = "${DeployConfig.awsEcrImg}"

                        sh script: """
                                clArn=\$(aws ecs list-clusters | jq -r '.clusterArns[]|select(test("^.*CL.*-'${awsEnv}'\$"))')
                                svcArn=\$(aws ecs list-services --cluster \${clArn} | jq -r '.serviceArns[]|select(test("^.*SVC-'${awsAppName}'"))')
                                svcTaskDefArn=\$(aws ecs describe-services  --cluster \${clArn} --services \${svcArn} | jq -r '.services[].taskDefinition')
                                echo \${svcTaskDefArn}
                            """
                }
        }
} 
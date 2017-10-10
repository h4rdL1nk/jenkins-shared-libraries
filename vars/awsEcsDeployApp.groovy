
def call(Map DeployConfig){
        withAWS(region:"${DeployConfig.awsRegion}",credentials:"${DeployConfig.awsCredId}"){
                script{
                        awsEnv = "${DeployConfig.awsAppEnv}"
                        awsAppName = "${DeployConfig.awsAppEnv}"
                        awsEcrImg = "${DeployConfig.awsAppEnv}""

                        def AwsCluster = sh script: """
                            aws ecs list-clusters | jq -r '.clusterArns[]|select(test("^.*CL.*-${awsEnv}\$"))'
                            """, returnStdout: true
                        def AwsService = sh script: """
                            aws ecs list-services --cluster ${AwsCluster.trim()} | jq -r '.serviceArns[]|select(test("^.*:service/SVC-${awsAppName}(-${awsEnv})?.*\$"))'
                            """, returnStdout: true
                        def AwsTaskDef = sh script: """
                            aws ecs describe-services --cluster ${AwsCluster.trim()} --services ${AwsService.trim()} | jq -r '.services[].deployments[].taskDefinition'
                            """, returnStdout: true
                        def AwsTaskDefJson = sh script: """
                            aws ecs describe-task-definition --task-definition ${AwsTaskDef.trim()} | jq -rc '.taskDefinition|.containerDefinitions[].image="'${awsEcrImg}'"|if .volumes != null then .volumes=.volumes else .volumes=[] end|if .networkMode != null then .networkMode=.networkMode else .networkMode="bridge" end|if .placementConstraints != null then .placementConstraints=.placementConstraints else .placementConstraints=[] end|{family:.family,containerDefinitions:.containerDefinitions,volumes:.volumes,placementConstraints:.placementConstraints,networkMode:.networkMode}'
                            """, returnStdout: true
                        def AwsTaskDefArn = sh script: """
                            aws ecs register-task-definition --cli-input-json '${AwsTaskDefJson.trim()}' | jq -r '.taskDefinition.taskDefinitionArn'
                            """, returnStdout: true
                        def AwsSvcUpdatedTask = sh script: """
                            aws ecs update-service --cluster ${AwsCluster.trim()} --service ${AwsService.trim()} --task-definition ${AwsTaskDefArn.trim()} | jq -r '.service.taskDefinition'
                            """, returnStdout: true

                        echo "Service updated with ${AwsSvcUpdatedTask.trim()}"

                        echo "Finished build ${JOB_NAME}:${BUILD_NUMBER} at node ${NODE_NAME}"
                }
        }
}
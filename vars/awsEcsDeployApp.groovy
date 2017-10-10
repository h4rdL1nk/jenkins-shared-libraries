
def call(Map DeployConfig){
        withAWS(region:"${DeployConfig.awsRegion}",credentials:"${DeployConfig.awsCredId}"){
                script{
                        awsEnv = "${DeployConfig.awsAppEnv}"
                        awsAppName = "${DeployConfig.awsAppEnv}"
                        awsEcrImg = "${DeployConfig.awsAppEnv}""

                        sh script: """
                            #!/bin/bash

                            aws ecs list-clusters | jq -r '.clusterArns[]|select(test("^.*CL.*-${awsEnv}\$"))'
                            
                            #aws ecs list-services --cluster ${AwsCluster.trim()} | jq -r '.serviceArns[]|select(test("^.*:service/SVC-${awsAppName}(-${awsEnv})?.*\$"))'
                            #aws ecs describe-services --cluster ${AwsCluster.trim()} --services ${AwsService.trim()} | jq -r '.services[].deployments[].taskDefinition'
                            #aws ecs describe-task-definition --task-definition ${AwsTaskDef.trim()} | jq -rc '.taskDefinition|.containerDefinitions[].image="'${awsEcrImg}'"|if .volumes != null then .volumes=.volumes else .volumes=[] end|if .networkMode != null then .networkMode=.networkMode else .networkMode="bridge" end|if .placementConstraints != null then .placementConstraints=.placementConstraints else .placementConstraints=[] end|{family:.family,containerDefinitions:.containerDefinitions,volumes:.volumes,placementConstraints:.placementConstraints,networkMode:.networkMode}'
                            #aws ecs register-task-definition --cli-input-json '${AwsTaskDefJson.trim()}' | jq -r '.taskDefinition.taskDefinitionArn'
                            #aws ecs update-service --cluster ${AwsCluster.trim()} --service ${AwsService.trim()} --task-definition ${AwsTaskDefArn.trim()} | jq -r '.service.taskDefinition'
                            """, returnStdout: true
                }
        }
}

def call(Map DeployConfig){
        withAWS(region:"${DeployConfig.awsRegion}",credentials:"${DeployConfig.awsCredId}"){
                script{
                        awsEnv = "${DeployConfig.awsAppEnv}"
                        awsAppName = "${DeployConfig.awsAppName}"
                        awsEcrImg = "${DeployConfig.awsEcrImg}"

                        sh script: """
                                #!/bin/bash

                                clArn=\$(aws ecs list-clusters | jq -r '.clusterArns[]|select(test("^.*CL.*-'${awsEnv}'\$"))' 2>/dev/null)

                                [ -z \${clArn} ] && echo ECS/Cluster no encontrado && exit 1

                                svcArn=\$(aws ecs list-services --cluster \${clArn} \
                                          | jq -r '.serviceArns[]|select(test("^.*SVC-'${awsAppName}'"))')

                                [ -z \${svcArn} ] && echo ECS/Service no encontrado && exit 2

                                svcTaskDefArn=\$(aws ecs describe-services  --cluster \${clArn} --services \${svcArn} \
                                          | jq -r '.services[].taskDefinition')

                                svcTaskDefJson=\$(aws ecs describe-task-definition --task-definition \${svcTaskDefArn})

                                modSvcTaskDefJson=\$(echo \${svcTaskDefJson} \
                                    | jq -r '.taskDefinition|
                                            if .volumes 
                                                    then .volumes=.volumes 
                                                    else .volumes=[] 
                                                    end|
                                            if .placementConstraints != null 
                                                    then .placementConstraints=.placementConstraints 
                                                    else .placementConstraints=[] 
                                                    end|
                                            .containerDefinitions[].image="'${awsEcrImg}'"|
                                            {
                                                networkMode:.networkMode,
                                                family:.family,
                                                volumes:.volumes,
                                                containerDefinitions:.containerDefinitions,
                                                placementConstraints:.placementConstraints
                                            }')

                                svcNewTaskDefArn=\$(aws ecs register-task-definition --cli-input-json "\${modSvcTaskDefJson}" \
                                    | jq -r '.taskDefinition.taskDefinitionArn')

                                svcUpdateResult=\$(aws ecs update-service \
                                    --cluster \${clArn} \
                                    --service \${svcArn} \
                                    --task-definition \${svcNewTaskDefArn} \
                                    | jq -r '.service.deployments[]|select(.status=="PRIMARY")|.taskDefinition')
                            """
                }
        }
} 
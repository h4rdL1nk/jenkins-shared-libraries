
def call(Map DeployConfig){
        withAWS(region:"${DeployConfig.awsRegion}",credentials:"${DeployConfig.awsCredId}"){
                script{
                        awsEnv = "${DeployConfig.awsAppEnv}"
                        awsAppName = "${DeployConfig.awsAppName}"
                        awsEcrImg = "${DeployConfig.awsEcrImg}"

                        sh script: """
                                #!/bin/bash

                                clArn=\$(aws ecs list-clusters | jq -r '.clusterArns[]|select(test("^.*CL.*-'${awsEnv}'\$"))' 2>/dev/null)

                                [ \$? -ne 0 ] && echo error recuperando ECS/Cluster && exit 2
                                [ -z \${clArn} ] && echo ECS/Cluster no encontrado && exit 1

                                svcArn=\$(aws ecs list-services --cluster \${clArn} \
                                          | jq -r '.serviceArns[]|select(test("^.*SVC-'${awsAppName}'"))' 2>/dev/null)

                                [ \$? -ne 0 ] && echo error recuperando ECS/Service && exit 2
                                [ -z \${svcArn} ] && echo ECS/Service no encontrado && exit 1

                                svcTaskDefArn=\$(aws ecs describe-services  --cluster \${clArn} --services \${svcArn} \
                                          | jq -r '.services[].taskDefinition' 2>/dev/null)

                                [ \$? -ne 0 ] && echo error recuperando ECS/TaskDefinition && exit 2
                                [ -z \${svcTaskDefArn} ] && echo ECS/TaskDefinition no encontrada && exit 1

                                svcTaskDefJson=\$(aws ecs describe-task-definition --task-definition \${svcTaskDefArn} 2>/dev/null)                                

                                [ \$? -ne 0 ] && echo error recuperando ECS/TaskDefinition && exit 2

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
                                            if .networkMode
                                                    then .networkMode=.networkMode
                                                    else .networkMode="bridge"
                                                    end|
                                            .containerDefinitions[].image="'${awsEcrImg}'"|
                                            {
                                                networkMode:.networkMode,
                                                family:.family,
                                                volumes:.volumes,
                                                containerDefinitions:.containerDefinitions,
                                                placementConstraints:.placementConstraints
                                            }')

                                [ \$? -ne 0 ] && echo error construyendo ECS/TaskDefinition && exit 2

                                svcNewTaskDefArn=\$(aws ecs register-task-definition --cli-input-json "\${modSvcTaskDefJson}" \
                                    | jq -r '.taskDefinition.taskDefinitionArn' 2>/dev/null)

                                [ \$? -ne 0 ] && echo error registrando ECS/TaskDefinition && exit 2

                                svcUpdateResult=\$(aws ecs update-service \
                                    --cluster \${clArn} \
                                    --service \${svcArn} \
                                    --task-definition \${svcNewTaskDefArn} \
                                    | jq -r '.service.deployments[]|select(.status=="PRIMARY")|.taskDefinition' 2>/dev/null)

                                [ \$? -ne 0 ] && echo error actualizando ECS/Service && exit 2
                            """
                }
        }
} 
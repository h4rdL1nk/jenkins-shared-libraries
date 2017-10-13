
def call(Map DeployConfig){
        withAWS(region:"${DeployConfig.awsRegion}",credentials:"${DeployConfig.awsCredId}"){
                script{
                        awsEnv = "${DeployConfig.awsAppEnv}"
                        awsAppName = "${DeployConfig.awsAppName}"
                        awsEcrImg = "${DeployConfig.awsEcrImg}"
                        deployTimeout = "${DeployConfig.deployTimeout}"

                        sh script: """
                                #!/bin/bash

                                clArn=\$(aws ecs list-clusters | jq -r '.clusterArns[]|select(test("^.*/CL.*-'${awsEnv}'\$"))' 2>/dev/null)

                                [ \$? -ne 0 ] && echo error recuperando ECS/Cluster && exit 2
                                [ -z \${clArn} ] && echo ECS/Cluster no encontrado && exit 1

                                svcArn=\$(aws ecs list-services --cluster \${clArn} \
                                          | jq -r '.serviceArns[]|select(test("^.*/SVC-'${awsAppName}'"))' 2>/dev/null)

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

                                if [ \$? -ne 0 ]
                                then
                                    echo Error actualizando ECS/Service
                                    exit 2
                                else
                                    echo Updated ECS/Service: \${svcUpdateResult}
                                fi

                                i=0

                                while :
                                do
                                      RUNNING=\$(aws ecs list-tasks --cluster \${clArn}  --service-name \${svcArn} --desired-status RUNNING \
                                        | jq -r '.taskArns[]' \
                                        | xargs -I{} aws ecs describe-tasks --cluster \${clArn} --tasks {} \
                                        | jq -r '.tasks[]| if .taskDefinitionArn == "'\${svcNewTaskDefArn}'" then . else empty end|.lastStatus' \
                                        | grep -e RUNNING || : )

                                      if `echo \$RUNNING | grep RUNNING 1>/dev/null 2>/dev/null`
                                      then
                                            echo Service deployed
                                            exit 0
                                      fi

                                      if [ \$i -ge ${deployTimeout} ]
                                      then
                                            echo Deployment timeout[${deployTimeout}]!!
                                            exit 1
                                      fi

                                      sleep 10
                                      i=\$(( \$i + 10 ))
                                done
                                
                            """
                }
        }
} 
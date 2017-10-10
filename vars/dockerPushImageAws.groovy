def call(Map PushConfig){

        withAWS(region:"${PushConfig.awsRegion}",credentials:"${PushConfig.awsCredId}"){
                script {
                        def appName = PushConfig.pushImageTag.split(':')[1].trim()
                        //def create_repo_cmd = sh script: "aws ecr create-repository --repository-name ${APP_NAME}"
                        def docker_login_cmd = sh script: "aws ecr get-login --no-include-email", returnStdout: true
                        def docker_login_endpt = docker_login_cmd.split(' ')[7].trim()

                        imgAwsTag = "${docker_login_endpt.split('//')[1]}/${PushConfig.pushImageTag}"

                        sh script: """
                            ${docker_login_cmd}
                            docker tag ${PushConfig.localImageTag} ${imgAwsTag}
                            docker push ${imgAwsTag}
                            """, returnStdout: true
                    }
        }

}
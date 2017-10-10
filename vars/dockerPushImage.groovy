

def call(registry){ 

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

        withDockerRegistry(url:${config.registryUrl},credentialsId:"local-docker-registry"){
				script{
						imgTag = codeCo.GIT_COMMIT
						imgLocalTag = "registry.madisonmk.com/${DEPARTMENT}/${APP_NAME}:${imgTag}"
						sh script: """
                            docker tag jenkins-${JOB_NAME}-${BUILD_NUMBER}-img ${imgLocalTag}
                            docker push ${imgLocalTag}
                            """, returnStdout: true
				}
		}
}
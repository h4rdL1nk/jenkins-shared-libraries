

def call(registryUrl,imageOrigName,imagePushName){ 
        withDockerRegistry(url:"${registryUrl}",credentialsId:"local-docker-registry"){
				script{
						imgLocalTag = "registry.madisonmk.com/${imageName}"
						sh script: """
                            docker tag ${imageOrigName} ${imgLocalTag}
                            docker push ${imgLocalTag}
                            """, returnStdout: true
				}
		}
}
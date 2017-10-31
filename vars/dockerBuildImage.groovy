def call(Map BuildConfig){

    script{

        def options = "--no-cache --pull --force-rm"
        def tag = BuildConfig.tag
        def extraOptions = BuildConfig.options
        def buildDir = BuildConfig.buildDir

        sh script: """
            #!/bin/bash
            set +x 
            docker build -t ${tag} ${options} ${extraOptions} ${buildDir}
        """

    }

}
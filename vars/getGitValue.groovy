#!/usr/bin/env groovy

def call(Map gitConfig){

    def data = ''

    switch(gitConfig.param){

        case 'authorMail':
            data = sh(
                returnStdout: true, 
                script: """
                            #!/bin/bash
                            set +x
                            git log -n 1 --pretty=format:'%aE'
                        """
            ).trim()
            break

        case 'currentBranch':
            data = sh(
                returnStdout: true, 
                script: """
                            #!/bin/bash
                            set +x
                            git rev-parse --abbrev-ref HEAD
                        """
            ).trim()
            break

        case 'longHash':
            data = sh(
                returnStdout: true, 
                script: """
                            #!/bin/bash
                            set +x
                            git log -n 1 --pretty=format:'%H'
                        """
            ).trim()
            break

        case 'shortHash':
            data = sh(
                returnStdout: true, 
                script: """
                            #!/bin/bash
                            set +x
                            git log -n 1 --pretty=format:'%h'
                        """
            ).trim()
            break

        case 'commitDate':
            data = sh(
                returnStdout: true, 
                script: """
                            #!/bin/bash
                            set +x
                            git log -n 1 --pretty=format:'%ci'
                        """
            ).trim()
            break

        case 'message':
            data = sh(
                returnStdout: true, 
                script: """
                            #!/bin/bash
                            set +x
                            git log -n 1 --pretty=format:'%s'
                        """
            ).trim()
            break

    }

    return data

}
#!/usr/bin/env groovy

def call(Map notifyConfig){

    switch(notifyConfig.type){

        case 'slack-default-start':
            commitMsg = getGitValue([
                param: "message"
            ])
            commitHash = getGitValue([
                param: "shortHash"
            ])
            commitDate = getGitValue([
                param: "commitDate"
            ])
            
            slackSend channel: '#ci-jobs', 
                color: '#6CBDEC', 
                message: "Starting build job ${JOB_NAME} #${BUILD_NUMBER} (<${BUILD_URL}|Open>)\n[${commitDate}] `${commitHash}` message:\n ```${commitMsg}```"
        
            break

        case 'slack-default-end':
            def buildResult = currentBuild.getCurrentResult()
            def slackColor = ""

            switch (buildResult){
                    case "FAILURE":
                        slackColor = "#F71302";
                        break;
                    case "SUCCESS":
                        slackColor = "#6BF702";
                        break;
                    default:
                        slackColor = "#6CBDEC";
            }

            slackSend channel: "#ci-jobs", 
                      color: "${slackColor}", 
                      message: "Built job ${JOB_NAME} #${BUILD_NUMBER} with result *${buildResult}*"
            
            break

    }

}
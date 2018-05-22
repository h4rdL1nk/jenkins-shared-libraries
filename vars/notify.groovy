#!/usr/bin/env groovy

def call(Map notifyConfig){

    switch(notifyConfig.type){

        case 'slack-default-start':

            def slackMessage = ""
            def slackChannel = ""
            def gitInfo = ""

            if(notifyConfig.channel){
                slackChannel = "${notifyConfig.channel}"
            }
            else{
                slackChannel = "#ci-jobs"
            }


            //Get git commit properties
            commitMsg = getGitValue([
                param: "message"
            ])
            commitHash = getGitValue([
                param: "shortHash"
            ])
            commitDate = getGitValue([
                param: "commitDate"
            ])

            //Set git information string for message
            if(env.GIT_COMMIT) {
                gitInfo = "${GIT_URL} (${GIT_BRANCH})\n[${commitDate}] `${commitHash}` message:\n ```${commitMsg}```"
            }
            
            //Compose slack message string
            if(notifyConfig.message){
                slackMessage = "Starting build job *${JOB_NAME}* #${BUILD_NUMBER} (<${BUILD_URL}|Open>)\n${gitInfo}\n*INFO*: ${notifyConfig.message}"
            }
            else{
                slackMessage = "Starting build job *${JOB_NAME}* #${BUILD_NUMBER} (<${BUILD_URL}|Open>)\n${gitInfo}"
            } 
            
            //Send slack notification
            slackSend channel: "${slackChannel}", 
                color: '#6CBDEC', 
                message: "${slackMessage}"
        
            break

        case 'slack-default-end':

            def buildResult = currentBuild.getCurrentResult()
            def slackColor = ""
            def slackChannel = ""
            def slackMessage = ""

            if(notifyConfig.channel){
                slackChannel = "${notifyConfig.channel}"
            }
            else{
                slackChannel = "#ci-jobs"
            }

            if(notifyConfig.message){
                slackMessage = "Built job *${JOB_NAME}* #${BUILD_NUMBER} with result *${buildResult}*\n*INFO*: ${notifyConfig.message}"
            }
            else{
                slackMessage = "Built job *${JOB_NAME}* #${BUILD_NUMBER} with result *${buildResult}*"
            }

            //Set colors by build result
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

            //Send slack notification
            slackSend channel: "${slackChannel}", 
                      color: "${slackColor}", 
                      message: "${slackMessage}"
            
            break

    }

}
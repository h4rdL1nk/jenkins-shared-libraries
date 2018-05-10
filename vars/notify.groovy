#!/usr/bin/env groovy

def call(Map notifyConfig){

    switch(notifyConfig.type){

        case 'slack-default-start':

            def slackMessage = ""
            def gitInfo = ""

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
            gitInfo = "${GIT_URL} (${GIT_BRANCH})\n[${commitDate}] `${commitHash}` message:\n ```${commitMsg}```"

            //Compose slack message string
            slackMessage = "Starting build job *${JOB_NAME}* #${BUILD_NUMBER} (<${BUILD_URL}|Open>)\n${gitInfo}" 
            
            //Send slack notification
            slackSend channel: '#ci-jobs', 
                color: '#6CBDEC', 
                message: "${slackMessage}"
        
            break

        case 'slack-default-end':
            def buildResult = currentBuild.getCurrentResult()
            def slackColor = ""

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
            slackSend channel: "#ci-jobs", 
                      color: "${slackColor}", 
                      message: "Built job *${JOB_NAME}* #${BUILD_NUMBER} with result *${buildResult}*"
            
            break

    }

}
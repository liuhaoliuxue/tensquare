//git凭证ID
def git_auth = "b02667a5-ddcc-4826-b2e1-3c852f0529d2"

//git的url地址
def git_url = "git@192.168.1.133:itheima_group/microservice.git"
//镜像版本号
def tag = "latest"
//定义harbor的地址
def harbor_url = "192.168.1.136:1080"
//定义私有库名称
def harbor_project = "tensquare"
//定义harbor的认证信息
def harbor_auth = "cb4c4d1f-f2f5-4ac0-a2ae-c08a1661835f"


node {

    stage('拉取代码') {
      checkout([$class: 'GitSCM', \
      branches: [[name: "*/${branch}"]], \
      doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [],\
      userRemoteConfigs: [[credentialsId: "${git_auth}", url: "${git_url}"]]])
   }
    stage('代码审查') {
      //引入一个名字叫sonar-scanner的全局工具
      def scannerHome = tool 'sonar-scanner'
      //引入一个名字叫sonarqube的系统配置（sonarqube服务器环境）
      withSonarQubeEnv('sonarqube') {
      //引入上面定义的全局变量
      sh """
        cd ${project_name}
        ${scannerHome}/bin/sonar-scanner
         """
      }
    }
    stage('编译，安装公共子工程') {
      sh "mvn -f tensquare_common clean install"
    }
    stage('编译，打包微服务工程') {
      // dockerfile:build会触发pom.xml中dockerfile-maven插件执行
      sh "mvn -f ${project_name} clean package dockerfile:build"
      
      //定义镜像名字
      def imageName = "${project_name}:${tag}"
      
      //对镜像打标签
      sh "docker tag ${imageName} ${harbor_url}/${harbor_project}/${imageName}"
      
      //把镜像推送到harbor仓库（使用凭证的方式登录）
      withCredentials([usernamePassword(credentialsId: "${harbor_auth}", passwordVariable: 'password', usernameVariable: 'username')]) {
        //使用凭证的方式登录harbor
        sh "docker login -u ${username} -p ${password} ${harbor_url}"
        //镜像的上传
        sh "docker push ${harbor_url}/${harbor_project}/${imageName}"
      
        sh "echo '镜像已经上传成功'"
      }
      //部署应用
      sshPublisher(publishers: [sshPublisherDesc(configName: 'centos5', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: "/ecapp/jenkins_shell/deploy.sh $harbor_url $harbor_project $project_name $tag $port", execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
    }

}
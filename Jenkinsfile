pipeline {
    agent any
    environment {
        //be sure to replace "willbla" with your own Docker Hub username
        DOCKER_IMAGE_NAME = "prakashganesan/javaspring"
        CANARY_REPLICAS = 0
    }
    stages {
        stage('Build') {
            steps {
                echo 'Running build automation'
                sh '/opt/apache-maven-3.2.5/bin/mvn clean package'
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }
        stage('Build Docker Image') {
            when {
                branch 'master'
            }
            steps {
                script {
                    app = docker.build(DOCKER_IMAGE_NAME)
                    app.inside {
                        sh 'echo Hello, World!'
                    }
                }
            }
        }
        stage('Push Docker Image') {
            when {
                branch 'master'
            }
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker_hub_login') {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
        stage('CanaryDeploy') {
            when {
                branch 'master'
            }
            environment { 
                CANARY_REPLICAS = 1
            }
            steps {
                sh '/usr/local/bin/helm repo update'
                sh '/usr/local/bin/helm upgrade --install nodecanary javaspring/nodecanary --tls'
            }
        }
        stage('SmokeTest') {
            when {
                branch 'master'
            }
            steps {
                script {
                    sleep (time: 60)
                    def response = httpRequest (
                        url: "http://$KUBE_MASTER_IP:30005",
                        timeout: 30
                    )
                    if (response.status != 200) {
                        error("Smoke test against canary deployment failed.")
                    }
                }
            }
        }
        stage('DeployToProduction') {
            when {
                branch 'master'
            }
            steps {
                input 'Deploy to Production?'
                milestone(1)
                sh '/usr/local/bin/helm repo update'
                sh '/usr/local/bin/helm upgrade --install prodapp javaspring/prodapp --tls'
            }
        }
    }
    post {
        cleanup {
            sh '/usr/local/bin/helm repo update'
            sh '/usr/local/bin/helm delete --purge nodecanary --tls'
        }
    }
}

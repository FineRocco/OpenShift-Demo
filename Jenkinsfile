pipeline {
    agent {
        kubernetes {
            defaultContainer 'maven'
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: quay.io/openshift/origin-jenkins-agent-maven:v4.0.0
    command:
    - cat
    tty: true
'''
        }
    }

    environment {
        // Application and OpenShift configuration
        APP_NAME         = 'openshift-demo'
        APP_NAMESPACE    = 'denis-ung-20-dev'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '=== Stage: SCM Checkout ==='
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                echo '=== Stage: Maven Build & Test ==='
                sh 'mvn clean package'
            }
            post {
                always {
                    // Archive test results if surefire reports are generated
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Deploy to OpenShift') {
            steps {
                echo '=== Stage: Deploy to OpenShift ==='
                sh '''
                    # Switch to project (ServiceAccount already has permissions)
                    echo "Ensuring project ${APP_NAMESPACE} exists..."
                    oc project ${APP_NAMESPACE}

                    # Apply Kubernetes/OpenShift resources
                    echo "Applying manifests..."
                    oc apply -f k8s/

                    # Trigger build using local directory and follow streaming logs
                    echo "Starting OpenShift Image Build from current workspace..."
                    oc start-build openshift-demo --from-dir=. --follow

                    # Wait for deployment to rollout
                    echo "Waiting for deployment..."
                    oc rollout status deployment/openshift-demo

                    # Print current route URL
                    echo "OpenShift deployment completed. External route:"
                    oc get route openshift-demo
                '''
            }
        }
    }

    post {
        success {
            echo '====================================================='
            echo ' Pipeline Execution Succeeded!'
            echo " openshift-demo successfully deployed to OpenShift.  "
            echo '====================================================='
        }
        failure {
            echo '====================================================='
            echo ' Pipeline Execution Failed!'
            echo ' Please check the console output above for error logs.'
            echo '====================================================='
        }
    }
}

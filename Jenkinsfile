pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    environment {
        // Application and OpenShift configuration
        APP_NAME         = 'openshift-demo'
        APP_NAMESPACE    = 'denis-ung-20-dev'
        IMAGE_TAG        = 'latest'

        // Credentials binding for OpenShift cluster connectivity
        // Configure these in Jenkins: Credentials -> System -> Global credentials
        OPENSHIFT_SERVER = credentials('openshift-server')
        OPENSHIFT_TOKEN  = credentials('openshift-token')
    }

    stages {
        stage('Checkout') {
            steps {
                echo '=== Stage: SCM Checkout ==='
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '=== Stage: Maven Build ==='
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo '=== Stage: Maven Test ==='
                sh 'mvn test'
            }
            post {
                always {
                    // Archive test results if surefire reports are generated
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Build Image') {
            steps {
                echo '=== Stage: Build Docker Image ==='
                sh 'docker build -t openshift-demo:latest .'
            }
        }

        stage('Deploy to OpenShift') {
            steps {
                echo '=== Stage: Deploy to OpenShift ==='
                sh '''
                    # 1. Authenticate to OpenShift cluster
                    echo "Logging into OpenShift cluster at ${OPENSHIFT_SERVER}..."
                    oc login --server="${OPENSHIFT_SERVER}" --token="${OPENSHIFT_TOKEN}" --insecure-skip-tls-verify=true

                    # 2. Switch to project or create it if missing
                    echo "Ensuring project ${APP_NAMESPACE} exists..."
                    oc project ${APP_NAMESPACE} || oc new-project ${APP_NAMESPACE}

                    # 3. Create binary build configuration if not already existing
                    echo "Ensuring binary BuildConfig exists..."
                    oc new-build --binary --name=openshift-demo -l app=openshift-demo || true

                    # 4. Trigger build using local directory and follow streaming logs
                    echo "Starting build from current workspace..."
                    oc start-build openshift-demo --from-dir=. --follow

                    # 5. Deploy application or rollout latest release
                    echo "Deploying application or rolling out latest..."
                    oc new-app openshift-demo -l app=openshift-demo || oc rollout latest openshift-demo

                    # 6. Expose service to create external route
                    echo "Exposing service as route..."
                    oc expose svc/openshift-demo || true

                    # 7. Print current route URL
                    echo "OpenShift deployment completed. External route:"
                    oc get route openshift-demo || true
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
        always {
            // Clean workspace to conserve agent disk space
            cleanWs deleteDirs: true, notFailBuild: true
        }
    }
}

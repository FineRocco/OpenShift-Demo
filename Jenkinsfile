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
    image: maven:3.9-eclipse-temurin-17
    command:
    - cat
    tty: true
    env:
    - name: HOME
      value: /tmp
  - name: ansible
    image: quay.io/ansible/creator-ee:v0.14.0
    command:
    - cat
    tty: true
    env:
    - name: HOME
      value: /tmp
'''
        }
    }

    environment {
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
        }

        stage('Deploy to OpenShift (via Ansible)') {
            steps {
                // Switch to the Ansible container we spun up
                container('ansible') {
                    echo '=== Stage: Deploy to OpenShift with Ansible ==='
                    sh '''
                        # 1. Download OpenShift CLI (oc) inside the Ansible container
                        curl -sL https://mirror.openshift.com/pub/openshift-v4/clients/ocp/latest/openshift-client-linux.tar.gz | tar -xz
                        mv oc /tmp/oc
                        chmod +x /tmp/oc
                        export PATH=$PATH:/tmp

                        # 2. Run the Ansible Playbook
                        ansible-playbook ansible/deploy-playbook.yml
                    '''
                }
            }
        }
    }
}
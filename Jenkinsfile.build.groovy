pipeline {
    /**
     *  This agent label is expected to resolve to
     *  https://jenkins.nexus-amana.com/computer/aquarium-openjdk11-maven3.6/
     */
    agent { label 'maven3.6' }

    parameters {
        string(
                name: 'MAVEN_NEXUS',
                defaultValue: 'move-sonatype',
                description: 'Username/password credentials ID for sonatype maven repository.'
        )
    }

    stages {
        stage('Compile and run tests') {
            steps {
                sh 'mvn -U clean compile'
            }
        }

        stage('Deploy Maven Artifacts to Sonatype') {
            when {
                branch 'master'
            }
            steps {
                withCredentials(
                        [usernamePassword(
                                credentialsId: params.MAVEN_NEXUS,
                                usernameVariable: 'MAVEN_NEXUS_USER',
                                passwordVariable: 'MAVEN_NEXUS_PASSWORD'
                        )]) {
                    sh 'mvn -B -U clean deploy -Dsonatype2.repo.username=${MAVEN_NEXUS_USER} -Dsonatype2.repo.password=${MAVEN_NEXUS_PASSWORD}'
                }
            }
        }
    }
}
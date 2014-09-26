__author__ = 'Niko'

URL = 'http://localhost:63342/web-page/api/queue.php'
user = 'snap'
password = 'password'
COMMAND = 'hadoop jar Spinn3rHadoop-0.0.1-SNAPSHOT.jar '

import os
import requests

#TODO security does not work!!!
response = requests.get(URL, auth=(user, password))
data = response.json()
print data

if data['newJob']:
    jobName = data['jobName']
    command = data['command']
    command = COMMAND + ' '.join(command)

    # convert to string
    command = command.encode('ascii','ignore')

    print jobName
    print command, type(command)

    os.system(command)
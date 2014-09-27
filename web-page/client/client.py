__author__ = 'Niko'

#URL = 'http://localhost:63342/web-page/api/queue.php'
URL = 'http://snap.stanford.edu/news-search/api/queue.php'
user = 'newssearchapi'
password = 'oreiherugfqiwefhweuhf93'
COMMAND = 'hadoop jar Spinn3rHadoop-0.0.1-SNAPSHOT.jar '

import os
import requests

#TODO security does not work!!!
response = requests.get(URL, auth=(user, password))
data = response.json()

if data['newJob']:
    jobName = data['jobName']
    command = data['command']
    command = COMMAND + ' '.join([command[str(j)] for j in sorted(int(i) for i in command.keys())])

    # convert to string
    command = command.encode('ascii','ignore')

    print jobName
    print command

    os.system(command)
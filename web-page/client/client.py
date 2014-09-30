__author__ = 'Niko'

WORKING_DIRECTORY = "./running_job"
URL = 'http://snap.stanford.edu/news-search/api/queue.php'
user = 'newssearchapi'
password = 'oreiherugfqiwefhweuhf93'
COMMAND = 'hadoop jar /afs/cs.stanford.edu/u/niko/news-search/target/Spinn3rHadoop-0.0.1-SNAPSHOT.jar '

import re
import os
import json
import time
import shutil
import ntpath
import requests
import subprocess


def load_file(link_, fname_):
    r = requests.get(link_, auth=(user, password))
    f = open(fname_, 'w')
    f.write(r.text)
    f.close()


def read_command():
    f = open('command', 'r')
    arg = ' '.join([i.strip() for i in f.readlines()])
    f.close()
    return arg


def write_to_file(file_, content):
    f = open(file_, 'w')
    f.write(content)
    f.close()


# copied form web, this is present in never versions of
# subprocess packet, but the one on server is to old
def check_output(*popenargs, **kwargs):
    process = subprocess.Popen(stdout=subprocess.PIPE, *popenargs, **kwargs)
    output, unused_err = process.communicate()
    retcode = process.poll()
    if retcode:
        cmd = kwargs.get("args")
        if cmd is None:
            cmd = popenargs[0]
        error = subprocess.CalledProcessError(retcode, cmd)
        error.output = output
        raise error
    return output
# end copied


def get_tracking_url(p_, ofile):
    while p_.poll() is None:
        time.sleep(1)
        print 'waiting for URL'
        for line in open(ofile).readlines():
            if 'The url to track the job' in line.strip():
                url = re.search("(?P<url>https?://[^\s]+)", line).group("url")
                return url


def report_progress(p_, ofile):
    while p_.poll() is None:
        progress = None
        time.sleep(3)
        for line in open(ofile).readlines():
            if 'INFO mapreduce.Job:  map' in line.strip():
                line = line.split(' ')
                progress = line[6]+' '+line[8]
        if progress:
            update_state(jobID, progress=progress)
            print 'progress is  ' + progress
        else:
            print 'waiting for progress'


def get_exit_status(ofile):
    succ = False
    for line in open(ofile).readlines():
        if 'completed successfully' in line.strip():
            succ = True
    return succ


def update_state(job_id, status=None, tracking=None, progress=None):
    url_ = URL + '/' + job_id

    # send URL
    data = {'action': 'update'}
    if status:
        data['status'] = status
    if tracking:
        data['tracking'] = tracking
    if progress:
        data['progress'] = progress

    data = json.dumps(data)
    print "DATA GOING OUT: ", data

    headers = {'Content-type': 'application/json',
               'X-HTTP-Method': 'PUT'}

    response = requests.post(url_, data=data, headers=headers, auth=(user, password))
    print response


    #while True:

# load json
response = requests.get(URL, auth=(user, password))
json_ = response.json()

# if there is a new job
if json_['newJob']:

    # create folder
    if os.path.exists(WORKING_DIRECTORY):
        shutil.rmtree(WORKING_DIRECTORY)
    os.mkdir(WORKING_DIRECTORY)
    os.chdir(WORKING_DIRECTORY)

    # get and store id
    jobID = json_['jobID']
    write_to_file('jobID', jobID)

    # get dependencies
    for link in json_['dependencies'].values():
        fname = ntpath.basename(link)
        print fname
        load_file(link, fname)

    # set status to submitted
    update_state(jobID, status='submitted')

    # run the job
    arguments = read_command()

    # execute
    outFile = 'hadoop-out.log'
    p = subprocess.Popen(COMMAND + arguments + ' &> ' + outFile, shell=True)

    # wait for URL
    #url = get_tracking_url(p, outFile)

    #update_state(jobID, tracking=url, status='running')

    # report progress map
    #report_progress(p, outFile)

    #if get_exit_status(outFile):
    #    update_state(jobID, status='success')
    #else:
    #    update_state(jobID, status='fail')

    # delete folder
    #os.chdir('..')
    #shutil.rmtree(WORKING_DIRECTORY)

    #time.sleep(15)

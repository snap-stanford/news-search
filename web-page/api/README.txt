news-search/api/queue.php GET => returns new job if any along with the dependencies needed to run the job
{
    'newJob' => true/false,
    'jobID' => $job->id ,
    'dependencies' => <list_of_URLs>
}

news-search/api/queue.php/queue_id PUT => update the info about the runnign job
{
    'action': 'update',
    'status': {'running', 'submitted', 'success', 'fail'}
    'tracking': <tracking_URL>
    'progress': <percentage_done_mapers percentage_done_reducers>
    'hadoop_out': <hadoop_main_output>
}
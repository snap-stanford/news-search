<?php
/**
 * Created by PhpStorm.
 * User: Niko
 * Date: 26/09/14
 * Time: 19:23
 */

$QUEUE_PATH = '../api/queue/';

class Job{
    public $id;
    private $path;

    public function __construct($id){
        global $QUEUE_PATH;

        $this->id = $id;
        $this->path = $QUEUE_PATH.$id.'/';
    }

    public function get_hadoop_link(){
        $url = '';
        $handle = fopen($this->path.'/hadoop-track', "r");
        if ($handle) {
            while (($line = fgets($handle)) !== false) {
                $url = str_replace(array("\n"), '', $line);
            }
        } else {
            //TODO report fail
            echo 'ERROR while reading command file';
        }
        fclose($handle);
        return $url;
    }

    public function get_results_link(){
        return "http://ilhadoop1.stanford.edu:50070/explorer.html#/user/niko/".$this->id;
    }

    public function set_start_date($date){
        $handle = fopen($this->path.'/date', "w");
        if ($handle) {
            fwrite($handle, $date."\n");
        } else {
            //TODO report fail
            echo 'ERROR while reading command file';
        }
        fclose($handle);
    }
    public function get_start_date(){
        $date = '';
        $handle = fopen($this->path.'/date', "r");
        if ($handle) {
            while (($line = fgets($handle)) !== false) {
                $line = str_replace(array("\n"), '', $line);
                $date = explode(' ', $line);
            }
        } else {
            //TODO report fail
            echo 'ERROR while reading command file';
        }
        fclose($handle);
        return $date;
    }

    public function get_progress(){
        $progress = '0%';
        if($this->is_running()){
            $handle = fopen($this->path.'/_RUNNING', "r");
            if ($handle) {
                while (($line = fgets($handle)) !== false) {
                    $progress = $line;
                }
            } else {
                //TODO report fail
                echo 'ERROR while reading command file';
            }
            fclose($handle);
        }
        return $progress;
    }

    public function is_new(){
        if(is_file($this->path.'_NEW')){
            return true;
        }else{
            return false;
        }
    }
    public function is_running(){
        if(is_file($this->path.'_RUNNING')){
            return true;
        }else{
            return false;
        }
    }
    public function is_done_ok(){
        if(is_file($this->path.'_SUCCESS')){
            return true;
        }else{
            return false;
        }
    }
    public function is_done_nok(){
        if(is_file($this->path.'_FAIL')){
            return true;
        }else{
            return false;
        }
    }

}

function get_job_list(){
    global $QUEUE_PATH;

    $all_jobs = array();
    if ($dh = opendir($QUEUE_PATH)) {
        while (($folder = readdir($dh)) !== false) {
            if(is_dir($QUEUE_PATH.'/'.$folder) && strncmp($folder, 'job_', 4) == 0){
                $job = new Job($folder);
                array_push($all_jobs, $job);
            }
        }
    }else{
        //TODO report
    }
    rsort($all_jobs);
    return $all_jobs;
}


?>
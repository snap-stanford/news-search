<?php
// http://php.net/manual/en/ref.dir.php
//echo "<pre>";
//var_dump($PROBLEMS);
//var_dump($NOTIFICATION);
//var_dump($_POST);
//var_dump($_FILES);
//echo "</pre>";

// Report all PHP errors (see changelog)
error_reporting(E_ALL);
ini_set('display_errors', '1');

// includes
include_once '../lib/job_handler.php';

/**
 * Global variables and settings
 */
date_default_timezone_set('America/Los_Angeles');
$QUEUE_PATH = '../api/queue/';
$FILE_SIZE_LIMIT = 10000;
$SLEEP = 5;
$PROBLEMS = false;
$NOTIFICATION = '';

/**
 * Write out command
 */
function write_command($path, $jobID){
    $file = fopen($path, 'w');

    // output
    fwrite($file, '-output '.$jobID."\n");

    // start and end
    $fields = array("start","end");
    foreach ($fields as $field){
        if(isset($_POST[$field])){
            fwrite($file, '-'.$field.' '.$_POST[$field]."\n");
        }
    }

    // content
    fwrite($file, '-content ');
    foreach(array('WEB', 'TW', 'FB') as $cnt){
        if(isset($_POST['content'.$cnt])){
            fwrite($file, $cnt.' ');
        }
    }
    fwrite($file, "\n");

    // write all white and black lists
    $fields = array('lang', 'url', 'keyword', 'title', 'content', 'quote');
    foreach ($fields as $f1){
        foreach(array('WL', 'BL') as $lst){
            $field = $f1.$lst;
            if(isset($_FILES[$field.'F']) && $_FILES[$field.'F']['error'] != UPLOAD_ERR_NO_FILE){
                fwrite($file, '-'.$field.' '.$field.'F.txt'."\n");
            }else{
                $empty = true;
                for ($i=1; $i<=5; $i++) {
                    $fld = $field.$i;
                    if(isset($_POST[$fld]) && $_POST[$fld] != ''){
                        if($empty){
                            fwrite($file, '-'.$field.' ');
                            $empty = false;
                        }
                        fwrite($file, '\''.$_POST[$fld].'\' ');
                    }
                }
                if(!$empty){
                    fwrite($file, "\n");
                }
            }
        }
    }

    // remove versions
    $empty = true;
    foreach(array('A', 'B', 'C', 'D', 'E') as $i) {
        if(isset($_POST['removeVersions'.$i])){
            if($empty){
                fwrite($file, '-removeVersions ');
                $empty = false;
            }
            fwrite($file, $i.' ');
        }
    }
    if(!$empty){
        fwrite($file, "\n");
    }

    // number of reducers
    if(isset($_POST['reducers'])){
        fwrite($file, '-reducers '.$_POST['reducers']."\n");
    }

    // boolean options
    $fields = array('removeNoLanguage', 'removeGarbled', 'removeUnparsableURL', 'removeEmptyTitle',
        'removeEmptyContent', 'removeNoQuotes', 'caseInsensitive', 'formatF5');
    foreach ($fields as $field) {
        if (isset($_POST[$field])) {
            fwrite($file, '-'.$field."\n");
        }
    }
    fclose($file);
}

/**
 * Copy input files to local folder
 */
function copy_uploaded_files($path){
    global $FILE_SIZE_LIMIT;
    $possibleFiles = array('langWLF', 'langBLF', 'urlWLF', 'urlBLF', 'keywordWLF', 'keywordBLF',
        'titleWLF', 'titleBLF', 'contentWLF', 'contentBLF', 'quoteWLF', 'quoteBLF');
    foreach($possibleFiles as $file){
        // if present
        if(isset($_FILES[$file]) && $_FILES[$file]['error'] != UPLOAD_ERR_NO_FILE) {
            //check size limits
            if ($_FILES[$file]['size'] > $FILE_SIZE_LIMIT ||
                $_FILES[$file]['error'] == UPLOAD_ERR_FORM_SIZE ||
                $_FILES[$file]['error'] == UPLOAD_ERR_INI_SIZE) {
                errmsg("your input file is too big!");
            } elseif ( $_FILES[$file]["error"] > 0) {
                errmsg("unknown error while copying input files (code ".$_FILES[$file]["error"].')!');
            } else {
                // if all ok move it to job folder
                if (!move_uploaded_file($_FILES[$file]['tmp_name'], $path.$file.'.txt')){
                    errmsg("error while copying files from temp directory!");
                }
                else{
                    // hack, store filename to post, so you can show it in form
                    $_POST[$file] = $file.'.txt';
                }
            }
        }
    }
}

/** Errors */
function errmsg($msg) {
    global $PROBLEMS;
    global $NOTIFICATION;

    $PROBLEMS = true;
    $NOTIFICATION = '';
    $NOTIFICATION .= "<div class='alert alert-dismissable alert-danger'>\n";
    $NOTIFICATION .= "<h2 class='text-danger space-after-title'>Error while submitting a job!</h2>\n";
    $NOTIFICATION .= "<p>There was an error while submitting your job.</p>\n";
    $NOTIFICATION .= "<p>The error message is: <b>$msg</b></p>\n";
    $NOTIFICATION .= "<p>Please, <a href=\"javascript:history.go(-1)\">go back</a>, edit the form and try to re-submit the job.</p>";
    $NOTIFICATION .= "</div>\n";
}

/** Success */
function sccmsg() {
    global $NOTIFICATION;

    $NOTIFICATION = '';
    $NOTIFICATION .= "<div class='alert alert-dismissable alert-success'>\n";
    $NOTIFICATION .= "<h2 class='text-success space-after-title'>Your job was successfully submitted!</h2>\n";
    $NOTIFICATION .= "<p>You will be redirected to results page in 5 seconds.</p>\n";
    $NOTIFICATION .= "</div>\n";
}

/** Recursively delete folder */
function rrmdir($dir) {
    foreach(glob($dir . '/*') as $file) {
        if(is_dir($file)) rrmdir($file); else unlink($file);
    } rmdir($dir);
}

/**
 * If something is submitted
 */
if (isset($_POST['search'])) {

    // make folder
    $timestamp = time();
    $jobID = 'job_'.date('Y-m-d\TH-i-s', $timestamp).'_U'.uniqid();
    $jobPath = $QUEUE_PATH.$jobID.'/';
    mkdir($jobPath);

    // store date
    $currentJOB = new Job($jobID);
    $currentJOB->set_start_date(date('Y-m-d H:i:s', $timestamp));

    // set state to new
    $currentJOB->set_to_new();

    // write command to file
    write_command($jobPath.'command', $jobID);

    // upload files
    copy_uploaded_files($jobPath);

    // testing purposes
    //errmsg('testing');

    if(!$PROBLEMS){
        sccmsg();
        $currentJOB->store_post_data($_POST);
        header("refresh:".$SLEEP.";url=results.php" );
    }
    else{
        rrmdir($jobPath);
    }
}

include_once "../lib/header.html";
echo $NOTIFICATION;
include_once "../lib/footer.html";
?>
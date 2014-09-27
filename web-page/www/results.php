<?php
include_once '../lib/header.html';
include_once '../lib/job_handler.php';

/**
 * Global variables and settings
 */
date_default_timezone_set('America/Los_Angeles');
$REFRESH_RATE = 10;
$PAGE = 1;

// pagination settings
$PER_PAGE = 10;
$MAX_PAGE_LINKS = 11; // should be odd
if(isset($_GET['page'])){
    $PAGE = $_GET['page'];
}

//header("refresh:".$REFRESH_RATE );

?>

    <div class="control-label">
        <h1 class="space-after-title">Search results</h1>
    </div>


    <table class="table table-striped table-hover">
        <thead>
        <tr>
            <th class="text-center">Date</th>
            <th class="text-center">Time</th>
            <th class="text-center">Progress</th>
            <th class="text-center">Hadoop tracking</th>
            <th class="text-center">Result files</th>
        </tr>
        </thead>
        <tbody>

        <?php
        // fill results from files
        $allJobs = get_job_list();
        $numPages = ceil(sizeof($allJobs)/$PER_PAGE);
        if($PAGE > $numPages){
            $PAGE = $numPages;
        }
        $jobs = array_slice($allJobs,($PAGE-1)*$PER_PAGE,$PER_PAGE);

        foreach($jobs as $job){
            $class = '';
            $status = '';
            if($job->is_new()){
                $class = 'warning';
                $status = 'QUEUED';
            }
            elseif($job->is_running()){
                $class = 'info';
                $status = 'RUNNING';
            }
            elseif($job->is_done_ok()){
                $class = 'success';
                $status = 'SUCCESS';
            }
            elseif($job->is_done_nok()){
                $class = 'danger';
                $status = 'FAILED';
            }
            $date = $job->get_start_date();

            echo "<tr class='$class' class='text-center'>\n";

            echo "<td class='text-center'>".$date[0]."</td>\n";
            echo "<td class='text-center'>".$date[1]."</td>\n";
            if($job->is_running()){
                echo "<td class='text-center'>\n";
                echo "<div class='progress progress-striped active center-block' style='margin-bottom: 0px; width: 100px'>\n";
                echo "<div class='progress-bar' style='width: ".$job->get_progress()."'></div>\n";
                echo "</div>\n";
                echo "</td>\n";
            }else{
                echo "<td class='text-center'>$status</td>\n";
            }
            if(!$job->is_new()){
                echo "<td class='text-center'><a target='_blank' href='".$job->get_hadoop_link()."'>link</a></td>\n";
                echo "<td class='text-center'><a target='_blank' href='".$job->get_results_link()."'>link</a></td>\n";
            }
            else{
                echo "<td class='text-center'></td>\n";
                echo "<td class='text-center'></td>\n";
            }
            echo "</tr>";
        }
        ?>
        </tbody>
    </table>

    <!-- Pagination -->
    <div class="pagination-centered" align="center">
        <ul class="pagination pagination-sm no_margin">
            <?php
            // calculate starting page
            $half = (($MAX_PAGE_LINKS-1)/2);
            $compensateRight = max($half - ($numPages - $PAGE), 0);
            $minp = max(1, $PAGE-$half-$compensateRight);

            // link for page 1
            if($PAGE == 1) {
                echo "<li class='active'><a href='#'>«</a ></li >";
            }
            else{
                echo "<li><a href='".$_SERVER['PHP_SELF']."?page=1'>«</a ></li >";
            }

            // five intermediate pages
            for ($i = 0; $i < $MAX_PAGE_LINKS; $i++){
                $p = $minp+$i;
                if($PAGE == $p){
                    echo "<li class='active'><a href='#'>".$p."</a></li>";
                }
                elseif($p <= $numPages){
                    echo "<li><a class='' href='".$_SERVER['PHP_SELF']."?page=".$p."'>".$p."</a></li>";
                }

            }

            // link for last page
            if($PAGE == $numPages) {
                echo "<li class='active'><a href='#'>»</a ></li >";
            }
            else{
                echo "<li><a href='".$_SERVER['PHP_SELF']."?page=".$numPages."'>»</a ></li >";
            }
            ?>
        </ul>
    </div>

<?php
include_once '../lib/footer.html';
?>
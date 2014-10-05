<?php
include_once "../lib/header.html";
include_once "../lib/job_handler.php";

if(isset($_GET['show_form_id'])){
    if(!job_exists($_GET['show_form_id'])){
        //redirect
        header('Location: index.php');
    }
    $SHOW = true;
    $job = new Job($_GET['show_form_id']);
    $DATA = $job->get_post_data();
}
else{
    $SHOW = false;
}


function insert_input($name){
    global $SHOW, $DATA, $job;

    if($SHOW){
        if(isset($DATA[$name])){
            echo '<a href="'.$job->get_dependency_link($DATA[$name]).'" target="_blank" >
                <input type="text" class="form-control" value="'.$DATA[$name].'" disabled></a>';
        }
        else{
            echo '<input type="text" class="form-control" value="" disabled></a>';
        }
    }else{
        echo '<input name="'.$name.'" id="'.$name.'" type="file" class="file" data-show-preview="false" data-show-upload="false">';
    }
}

function print_if_present($name, $placeholder){
    global $SHOW, $DATA;

    if($SHOW) {
        echo 'disabled ';
        if(isset($DATA[$name])) {
            echo 'value="' . $DATA[$name] . '" ';
        }
    } else {
        if($placeholder) {
            echo 'placeholder="eg. '.$placeholder.'" ';
        }
    }
}

function print_if_present_file($name){
    global $SHOW, $DATA;

    if($SHOW) {
        echo 'disabled="true" readonly="true"';
        if(isset($DATA[$name])) {
            echo 'value="' . $DATA[$name] . '" ';
        }
    }
}

function print_if_present_reducers($name, $default){
    global $SHOW, $DATA;

    if($SHOW) {
        echo 'disabled ';
        if(isset($DATA[$name])) {
            echo 'value="' . $DATA[$name] . '" ';
        }
    } else {
        if($default) {
            echo 'value="'.$default.'" ';
        }
    }
}

function set_if_present_checkbox($name, $default){
    global $SHOW, $DATA;

    if($SHOW) {
        echo 'disabled ';
        if(isset($DATA[$name]) && $DATA[$name] == 'on') {
            echo 'checked ';
        }
    } else {
        if($default) {
            echo 'checked ';
        }
    }
}
?>

    <div class="control-label">
        <h1 class="space-after-title">New search</h1>
        <legend>Instructions</legend>
        <div class="col-lg-offset-2">
            The search is based on the white and black list concept. Documents can be filtered by many different fields.
            When using white or black lists, the document must match with some pattern of the white list and can not
            match with any pattern of the black list for it to be propagated to results. You can think of this, as there is
            an 'or' between the white list's patterns. If you wish to use 'and', put the patters in the same field and separate
            them with double and (&&). For example 'black && white' will search for documents containing both words, 'black'
            and 'white'.
            <br>
            <br>
            The form can accept up to 5 patterns for each of the lists. When you wish to use more, use files.
            In a file, each pattern should be in its own line.
        </div>
        <br>
        <div class="alert alert-danger col-lg-offset-2 text-center">
            <strong style="font-size: 17px">Please, use the search wisely, since it occupies a lot of resources!</strong>
        </div>
    </div>

    <form enctype="multipart/form-data" class="form-horizontal" method="post" action="submit.php">
    <fieldset>

    <!-- Date -->
    <legend>Select date</legend>
    <div class="form-group">
        <label class="col-lg-2 control-label"></label>
        <div class="col-lg-10">
            We collect data from 1st of August 2008 onwards. Specify the start and end date in format <b>YYYY-MM-DDTHH</b>,
            where YYYY is year, MM is month (1-12), DD is day in month (1-31) and finally HH is hour of the day (0-23).
            Be sure tu put the separating 'T' between DD and HH.
        </div>
    </div>

    <div class="form-group">
        <label  class="col-lg-2 control-label">Start</label>
        <div class="col-lg-10">
            <input name="start" type="text" class="form-control" <?php print_if_present('start', '2008-08-01T01');?>>
        </div>
    </div>

    <div class="form-group">
        <label for="inputPassword" class="col-lg-2 control-label">End</label>
        <div class="col-lg-10">
            <input name="end" type="text" class="form-control" <?php print_if_present('end', '2014-08-01T01');?>>
        </div>
    </div>
    <!-- Date -->

    <!-- Content -->
    <legend>Select content</legend>
    <div class="form-group">
        <div class="col-lg-10 margin-left-content">

            <div class="checkbox">
                <label>
                    <input name="contentWEB" type="checkbox" <?php set_if_present_checkbox('contentWEB', true)?>> Web
                </label>
            </div>
            <div class="checkbox">
                <label>
                    <input name="contentTW" type="checkbox" <?php set_if_present_checkbox('contentTW', true)?>> Twitter
                </label>
            </div>
            <div class="checkbox">
                <label>
                    <input name="contentFB" type="checkbox" <?php set_if_present_checkbox('contentFB', true)?>> Facebook
                </label>
            </div>
            <div class="checkbox"></div>
        </div>
    </div>
    <!-- Content -->

    <!-- Language -->
    <legend>Select language</legend>
    <div class="form-group">
        <label class="col-lg-2 control-label"></label>
        <div class="col-lg-10">
            Each document can have several languages detected and we store probabilities for each of them.
            Document's <b>probable language</b> is defined as the language whose probability is >= 0.8.
            White and black lists work only on documents with a probable language.
        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-2 control-label">White list</label>
        <div class="col-lg-10">

            <div class="col-lg-2-no-padding padding-right-2">
                <input name="langWL1" type="text" class="form-control" id="langW1"
                    <?php print_if_present('langWL1', 'en');?>>
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="langWL2" type="text" class="form-control" id="langW2"
                    <?php print_if_present('langWL2', '');?>>
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="langWL3" type="text" class="form-control" id="langW3"
                    <?php print_if_present('langWL3', '');?>>
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="langWL4" type="text" class="form-control" id="langW4"
                    <?php print_if_present('langWL4', '');?>>
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="langWL5" type="text" class="form-control" id="langW5"
                    <?php print_if_present('langWL5', '');?>>
            </div>

            <?php insert_input('langWLF') ?>

        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-2 control-label">Black list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="langBL1" type="text" class="form-control" id="langB1"
                        <?php print_if_present('langBL1', 'vi');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="langBL2" type="text" class="form-control" id="langB2"
                        <?php print_if_present('langBL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="langBL3" type="text" class="form-control" id="langB3"
                        <?php print_if_present('langBL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="langBL4" type="text" class="form-control" id="langB4"
                        <?php print_if_present('langBL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="langBL5" type="text" class="form-control" id="langB5"
                        <?php print_if_present('langBL5', '');?>>
                </div>
            </div>

            <?php insert_input('langBLF') ?>

            <div class="checkbox col-lg-10">
                <label>
                    <input name="removeNoLanguage" type="checkbox" <?php set_if_present_checkbox('removeNoLanguage', true)?>>
                    Remove documents without a probable language
                </label>
            </div>
        </div>
    </div>

    <!-- Language -->

    <!-- URLs -->
    <legend>Filter by domain name</legend>
    <div class="form-group">
        <label class="col-lg-2 control-label"></label>
        <div class="col-lg-10">
            Documents have associated the source URL. Some, however, have invalid URLs
            and consequently the source domain name is unknown.
        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-2 control-label">White list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="urlWL1" type="text" class="form-control" id="urlW1"
                        <?php print_if_present('urlWL1', 'stanford.edu');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="urlWL2" type="text" class="form-control" id="urlW2"
                        <?php print_if_present('urlWL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="urlWL3" type="text" class="form-control" id="urlW3"
                        <?php print_if_present('urlWL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="urlWL4" type="text" class="form-control" id="urlW4"
                        <?php print_if_present('urlWL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="urlWL5" type="text" class="form-control" id="urlW5"
                        <?php print_if_present('urlWL5', '');?>>
                </div>
            </div>

            <?php insert_input('urlWLF') ?>

        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-2 control-label">Black list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="urlBL1" type="text" class="form-control" id="urlB1"
                        <?php print_if_present('urlBL1', 'spam.com');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="urlBL2" type="text" class="form-control" id="urlB2"
                        <?php print_if_present('urlBL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="urlBL3" type="text" class="form-control" id="urlB3"
                        <?php print_if_present('urlBL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="urlBL4" type="text" class="form-control" id="urlB4"
                        <?php print_if_present('urlBL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="urlBL5" type="text" class="form-control" id="urlB5"
                        <?php print_if_present('urlBL5', '');?>>
                </div>
            </div>

            <?php insert_input('urlBLF') ?>

            <div class="checkbox col-lg-10">
                <label>
                    <input name="removeUnparsableURL" type="checkbox" <?php set_if_present_checkbox('removeUnparsableURL', true)?>>
                    Remove documents without a source domain name
                </label>
            </div>
        </div>
    </div>
    <!-- URLs -->

    <!-- Keywords -->
    <legend>Filter by keywords</legend>
    <div class="form-group">
        <label class="col-lg-2 control-label"></label>
        <div class="col-lg-10">
            Keywords are sought for in the document's title and content.
        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-2 control-label">White list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="keywordWL1" type="text" class="form-control" id="keywordW1"
                        <?php print_if_present('keywordWL1', 'stanford');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="keywordWL2" type="text" class="form-control" id="keywordW2"
                        <?php print_if_present('keywordWL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="keywordWL3" type="text" class="form-control" id="keywordW3"
                        <?php print_if_present('keywordWL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="keywordWL4" type="text" class="form-control" id="keywordW4"
                        <?php print_if_present('keywordWL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="keywordWL5" type="text" class="form-control" id="keywordW5"
                        <?php print_if_present('keywordWL5', '');?>>
                </div>
            </div>

            <?php insert_input('keywordWLF') ?>

        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-2 control-label">Black list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="keywordBL1" type="text" class="form-control" id="keywordB1"
                        <?php print_if_present('keywordBL1', 'spam');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="keywordBL2" type="text" class="form-control" id="keywordB2"
                        <?php print_if_present('keywordBL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="keywordBL3" type="text" class="form-control" id="keywordB3"
                        <?php print_if_present('keywordBL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="keywordBL4" type="text" class="form-control" id="keywordB4"
                        <?php print_if_present('keywordBL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="keywordBL5" type="text" class="form-control" id="keywordB5"
                        <?php print_if_present('keywordBL5', '');?>>
                </div>
            </div>

            <?php insert_input('keywordBLF') ?>

        </div>
    </div>
    <!-- Keywords -->

    <!-- Title -->
    <legend>Filter by title</legend>
    <div class="form-group">
        <label class="col-lg-2 control-label">White list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="titleWL1" type="text" class="form-control" id="titleW1"
                        <?php print_if_present('titleWL1', 'Stanford');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="titleWL2" type="text" class="form-control" id="titleW2"
                        <?php print_if_present('titleWL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="titleWL3" type="text" class="form-control" id="titleW3"
                        <?php print_if_present('titleWL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="titleWL4" type="text" class="form-control" id="titleW4"
                        <?php print_if_present('titleWL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="titleWL5" type="text" class="form-control" id="titleW5"
                        <?php print_if_present('titleWL5', '');?>>
                </div>
            </div>

            <?php insert_input('titleWLF') ?>

        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-2 control-label">Black list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="titleBL1" type="text" class="form-control" id="titleB1"
                        <?php print_if_present('titleBL1', 'Spam');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="titleBL2" type="text" class="form-control" id="titleB2"
                        <?php print_if_present('titleBL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="titleBL3" type="text" class="form-control" id="titleB3"
                        <?php print_if_present('titleBL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="titleBL4" type="text" class="form-control" id="titleB4"
                        <?php print_if_present('titleBL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="titleBL5" type="text" class="form-control" id="titleB5"
                        <?php print_if_present('titleBL5', '');?>>
                </div>
            </div>

            <?php insert_input('titleBLF') ?>

            <div class="checkbox col-lg-10">
                <label>
                    <input name="removeEmptyTitle" type="checkbox" <?php set_if_present_checkbox('removeEmptyTitle', true)?>>
                    Remove documents without a title
                </label>
            </div>
        </div>
    </div>
    <!-- Title -->

    <!-- Content -->
    <legend>Filter by content</legend>
    <div class="form-group">
        <label class="col-lg-2 control-label">White list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="contentWL1" type="text" class="form-control" id="contentW1"
                        <?php print_if_present('contentWL1', 'Class');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="contentWL2" type="text" class="form-control" id="contentW2"
                        <?php print_if_present('contentWL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="contentWL3" type="text" class="form-control" id="contentW3"
                        <?php print_if_present('contentWL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="contentWL4" type="text" class="form-control" id="contentW4"
                        <?php print_if_present('contentWL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="contentWL5" type="text" class="form-control" id="contentW5"
                        <?php print_if_present('contentWL5', '');?>>
                </div>
            </div>

            <?php insert_input('contentWLF') ?>

        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-2 control-label">Black list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="contentBL1" type="text" class="form-control" id="contentB1"
                        <?php print_if_present('contentBL1', 'Spam');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="contentBL2" type="text" class="form-control" id="contentB2"
                        <?php print_if_present('contentBL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="contentBL3" type="text" class="form-control" id="contentB3"
                        <?php print_if_present('contentBL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="contentBL4" type="text" class="form-control" id="contentB4"
                        <?php print_if_present('contentBL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="contentBL5" type="text" class="form-control" id="contentB5"
                        <?php print_if_present('contentBL5', '');?>>
                </div>
            </div>

            <?php insert_input('contentBLF') ?>

            <div class="checkbox col-lg-10">
                <label>
                    <input name="removeEmptyContent" type="checkbox" <?php set_if_present_checkbox('removeEmptyContent', true)?>>
                    Remove documents without a content
                </label>
            </div>
        </div>
    </div>
    <!-- Content -->


    <!-- Quotes -->
    <legend>Filter by quotes</legend>
    <div class="form-group">
        <label class="col-lg-2 control-label">White list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="quoteWL1" type="text" class="form-control" id="quoteW1"
                        <?php print_if_present('quoteWL1', 'He said that ...');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="quoteWL2" type="text" class="form-control" id="quoteW2"
                        <?php print_if_present('quoteWL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="quoteWL3" type="text" class="form-control" id="quoteW3"
                        <?php print_if_present('quoteWL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="quoteWL4" type="text" class="form-control" id="quoteW4"
                        <?php print_if_present('quoteWL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="quoteWL5" type="text" class="form-control" id="quoteW5"
                        <?php print_if_present('quoteWL5', '');?>>
                </div>
            </div>

            <?php insert_input('quoteWLF') ?>

        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-2 control-label">Black list</label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input name="quoteBL1" type="text" class="form-control" id="quoteB1"
                        <?php print_if_present('quoteBL1', 'He did not say ...');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="quoteBL2" type="text" class="form-control" id="quoteB2"
                        <?php print_if_present('quoteBL2', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="quoteBL3" type="text" class="form-control" id="quoteB3"
                        <?php print_if_present('quoteBL3', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                    <input name="quoteBL4" type="text" class="form-control" id="quoteB4"
                        <?php print_if_present('quoteBL4', '');?>>
                </div>
                <div class="col-lg-2-no-padding padding-left-2">
                    <input name="quoteBL5" type="text" class="form-control" id="quoteB5"
                        <?php print_if_present('quoteBL5', '');?>>
                </div>
            </div>

            <?php insert_input('quoteBLF') ?>

            <div class="checkbox col-lg-10">
                <label>
                    <input name="removeNoQuotes" type="checkbox" <?php set_if_present_checkbox('removeNoQuotes', false)?>>
                    Remove documents without quotes
                </label>
            </div>
        </div>
    </div>
    <!-- Quotes -->

    <!-- Remove versions -->
    <legend>Remove versions</legend>
    <div class="form-group">
        <div class="col-lg-10 margin-left-content">

            <div class="checkbox">
                <label>
                    <input name="removeVersionsA" type="checkbox" <?php set_if_present_checkbox('removeVersionsA', false)?>> A
                </label>
            </div>
            <div class="checkbox">
                <label>
                    <input name="removeVersionsB" type="checkbox" <?php set_if_present_checkbox('removeVersionsB', false)?>> B
                </label>
            </div>
            <div class="checkbox">
                <label>
                    <input name="removeVersionsC" type="checkbox" <?php set_if_present_checkbox('removeVersionsC', false)?>> C
                </label>
            </div>
            <div class="checkbox">
                <label>
                    <input name="removeVersionsD" type="checkbox" <?php set_if_present_checkbox('removeVersionsD', false)?>> D
                </label>
            </div>
            <div class="checkbox">
                <label>
                    <input name="removeVersionsE" type="checkbox" <?php set_if_present_checkbox('removeVersionsE', false)?>> E
                </label>
            </div>
            <div class="checkbox"></div>
        </div>
    </div>
    <!-- Remove versions -->

    <!-- Select number of reducers -->
    <legend>Number of reducers</legend>
    <div class="form-group">
        <label class="col-lg-2 control-label"></label>
        <div class="col-lg-10">
            The number of reducers corresponds to the number of output files.
            Search will finish much faster if we do not use any reducers and the number of output files corresponds to the
            number of mappers. However, the number of output files can be enormous when the date range is large. To run the search without any reducers set the number of reducers to 0.
            <br><br>
            When the large number of files is not acceptable, use reducers. Then the time required is dependant on the number of documents found. Note that, if we find
            many documents and we use a small number of reducers (especially 1), the search will run very slow!
        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-2 control-label"></label>
        <div class="col-lg-10">
            <div class="">
                <div class="col-lg-2-no-padding padding-right-2">
                    <input class="form-control text-center" type="number" name="reducers" min="0" max="600"
                        <?php print_if_present_reducers('reducers', 40)?>>
                </div>
            </div>
        </div>
    </div>
    <!-- Select number of reducers -->

    <!-- Other -->
    <legend>Other</legend>
    <div class="form-group">
        <label class="col-lg-2 control-label"></label>
        <div class="col-lg-10">
            In some of the earlier version of the client, data was stored as ASCII or Latin1. Consequently, all some of the
            characters were not able to be stored properly (they are stored as '?' etc.) and were permanently lost.
            We say, that the documents is <b>grabled</b> if the fraction of useful (i.e. properly stored) characters
            in the document is below 0.8.
        </div>
    </div>

    <div class="form-group">
        <div class="col-lg-10 margin-left-content">

            <div class="checkbox">
                <label>
                    <input name="removeGarbled" type="checkbox" <?php set_if_present_checkbox('removeGarbled', true)?>>
                    Remove garbled documents
                </label>
            </div>
            <div class="checkbox">
                <label>
                    <input name="caseInsensitive" type="checkbox" <?php set_if_present_checkbox('caseInsensitive', false)?>>
                    Make all matching case insensitive
                </label>
            </div>
            <div class="checkbox">
                <label>
                    <input name="formatF5" type="checkbox" <?php set_if_present_checkbox('formatF5', false)?>>
                    Use one line per document output format (F5)
                </label>
            </div>
            <div class="checkbox"></div>
        </div>
    </div>
    <!-- Remove versions -->

    <input class="btn btn-success btn-lg center-block" type="submit" name="search" value="Search"
        <?php if($SHOW) {echo 'disabled';}?>/>

    </fieldset>
    </form>

<?php
include_once "../lib/footer.html";
?>
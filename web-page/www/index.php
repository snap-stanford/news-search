<?php
include_once "header.html";
?>

<div class="control-label">
    <h1 class="space-after-title">New search</h1>
    <legend>Instructions</legend>
    <p>TODO: write short instructions and caveats!</p>
</div>

<form enctype="multipart/form-data" class="form-horizontal" method="post" action="submit.php">
<fieldset>

<!-- Date -->
<legend>Select date</legend>
<div class="form-group">
    <label  class="col-lg-2 control-label">Start</label>
    <div class="col-lg-10">
        <input name="start" type="text" class="form-control" placeholder="eg. 2008-08-01T01">
    </div>
</div>
<div class="form-group">
    <label for="inputPassword" class="col-lg-2 control-label">End</label>
    <div class="col-lg-10">
        <input name="end" type="text" class="form-control" placeholder="eg. 2014-08-31T23">
    </div>
</div>
<!-- Date -->

<!-- Content -->
<legend>Select content</legend>
<div class="form-group">
    <div class="col-lg-10 margin-left-content">

        <div class="checkbox">
            <label>
                <input name="contentWEB" type="checkbox" checked> Web
            </label>
        </div>
        <div class="checkbox">
            <label>
                <input name="contentTW" type="checkbox" checked> Twitter
            </label>
        </div>
        <div class="checkbox">
            <label>
                <input name="contentFB" type="checkbox" checked> Facebook
            </label>
        </div>
        <div class="checkbox"></div>
    </div>
</div>
<!-- Content -->

<!-- Language -->
<legend>Select language</legend>
<div class="form-group">
    <label class="col-lg-2 control-label">White list</label>
    <div class="col-lg-10">

        <div class="col-lg-2-no-padding padding-right-2">
            <input name="langWL1" type="text" class="form-control" id="langW1" placeholder="eg. en">
        </div>
        <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
            <input name="langWL2" type="text" class="form-control" id="langW2" placeholder="">
        </div>
        <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
            <input name="langWL3" type="text" class="form-control" id="langW3" placeholder="">
        </div>
        <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
            <input name="langWL4" type="text" class="form-control" id="langW4" placeholder="">
        </div>
        <div class="col-lg-2-no-padding padding-left-2">
            <input name="langWL5" type="text" class="form-control" id="langW5" placeholder="">
        </div>

        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="langW">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="langWLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>
    </div>
</div>

<div class="form-group">
    <label class="col-lg-2 control-label">Black list</label>
    <div class="col-lg-10">
        <div class="">
            <div class="col-lg-2-no-padding padding-right-2">
                <input name="langBL1" type="text" class="form-control" id="langB1" placeholder="eg. vi">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="langBL2" type="text" class="form-control" id="langB2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="langBL3" type="text" class="form-control" id="langB3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="langBL4" type="text" class="form-control" id="langB4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="langBL5" type="text" class="form-control" id="langB5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="langB">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="langBLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>

        <div class="checkbox col-lg-10">
            <label>
                <input name="removeNoLanguage" type="checkbox" checked> Remove records without probable language
            </label>
        </div>
    </div>
</div>

<!-- Language -->

<!-- URLs -->
<legend>Filter by URL pattern</legend>
<div class="form-group">
    <label class="col-lg-2 control-label">White list</label>
    <div class="col-lg-10">
        <div class="">
            <div class="col-lg-2-no-padding padding-right-2">
                <input name="urlWL1" type="text" class="form-control" id="urlW1" placeholder="eg. stanford.edu">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="urlWL2" type="text" class="form-control" id="urlW2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="urlWL3" type="text" class="form-control" id="urlW3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="urlWL4" type="text" class="form-control" id="urlW4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="urlWL5" type="text" class="form-control" id="urlW5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="urlW">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="urlWLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>
    </div>
</div>

<div class="form-group">
    <label class="col-lg-2 control-label">Black list</label>
    <div class="col-lg-10">
        <div class="">
            <div class="col-lg-2-no-padding padding-right-2">
                <input name="urlBL1" type="text" class="form-control" id="urlB1" placeholder="eg. spam.com">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="urlBL2" type="text" class="form-control" id="urlB2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="urlBL3" type="text" class="form-control" id="urlB3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="urlBL4" type="text" class="form-control" id="urlB4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="urlBL5" type="text" class="form-control" id="urlB5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="urlB">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="urlBLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>

        <div class="checkbox col-lg-10">
            <label>
                <input name="removeUnparsableURL" type="checkbox" checked> Remove records with unparsable URL
            </label>
        </div>
    </div>
</div>
<!-- URLs -->

<!-- Keywords -->
<legend>Filter by keywords</legend>

<div class="form-group">
    <label class="col-lg-2 control-label">White list</label>
    <div class="col-lg-10">
        <div class="">
            <div class="col-lg-2-no-padding padding-right-2">
                <input name="keywordWL1" type="text" class="form-control" id="keywordsW1" placeholder="eg. stanford">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="keywordWL2" type="text" class="form-control" id="keywordsW2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="keywordWL3" type="text" class="form-control" id="keywordsW3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="keywordWL4" type="text" class="form-control" id="keywordsW4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="keywordWL5" type="text" class="form-control" id="keywordsW5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="keywordsW">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="keywordWLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>
    </div>
</div>

<div class="form-group">
    <label class="col-lg-2 control-label">Black list</label>
    <div class="col-lg-10">
        <div class="">
            <div class="col-lg-2-no-padding padding-right-2">
                <input name="keywordBL1" type="text" class="form-control" id="keywordsB1" placeholder="eg. spam">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="keywordBL2" type="text" class="form-control" id="keywordsB2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="keywordBL3" type="text" class="form-control" id="keywordsB3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="keywordBL4" type="text" class="form-control" id="keywordsB4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="keywordBL5" type="text" class="form-control" id="keywordsB5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="keywordsB">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="keywordBLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>
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
                <input name="titleWL1" type="text" class="form-control" id="titleW1" placeholder="eg. Stanford">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="titleWL2" type="text" class="form-control" id="titleW2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="titleWL3" type="text" class="form-control" id="titleW3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="titleWL4" type="text" class="form-control" id="titleW4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="titleWL5" type="text" class="form-control" id="titleW5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="titleW">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="titleWLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>
    </div>
</div>

<div class="form-group">
    <label class="col-lg-2 control-label">Black list</label>
    <div class="col-lg-10">
        <div class="">
            <div class="col-lg-2-no-padding padding-right-2">
                <input name="titleBL1" type="text" class="form-control" id="titleB1" placeholder="eg. Spam">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="titleBL2" type="text" class="form-control" id="titleB2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="titleBL3" type="text" class="form-control" id="titleB3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="titleBL4" type="text" class="form-control" id="titleB4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="titleBL5" type="text" class="form-control" id="titleB5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="titleB">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="titleBLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>

        <div class="checkbox col-lg-10">
            <label>
                <input name="removeEmptyTitle" type="checkbox" checked> Remove records without title
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
                <input name="contentWL1" type="text" class="form-control" id="contentW1" placeholder="eg. Class">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="contentWL2" type="text" class="form-control" id="contentW2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="contentWL3" type="text" class="form-control" id="contentW3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="contentWL4" type="text" class="form-control" id="contentW4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="contentWL5" type="text" class="form-control" id="contentW5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="contentW">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="contentWLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>
    </div>
</div>

<div class="form-group">
    <label class="col-lg-2 control-label">Black list</label>
    <div class="col-lg-10">
        <div class="">
            <div class="col-lg-2-no-padding padding-right-2">
                <input name="contentBL1" type="text" class="form-control" id="contentB1" placeholder="eg. Spam">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="contentBL2" type="text" class="form-control" id="contentB2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="contentBL3" type="text" class="form-control" id="contentB3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="contentBL4" type="text" class="form-control" id="contentB4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="contentBL5" type="text" class="form-control" id="contentB5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="contentB">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="contentBLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>

        <div class="checkbox col-lg-10">
            <label>
                <input name="removeEmptyContent" type="checkbox" checked> Remove records without content
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
                <input name="quoteWL1" type="text" class="form-control" id="quotesW1" placeholder="eg. He said that ...">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="quoteWL2" type="text" class="form-control" id="quotesW2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="quoteWL3" type="text" class="form-control" id="quotesW3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="quoteWL4" type="text" class="form-control" id="quotesW4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="quoteWL5" type="text" class="form-control" id="quotesW5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="quotesW">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="quoteWLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>
    </div>
</div>

<div class="form-group">
    <label class="col-lg-2 control-label">Black list</label>
    <div class="col-lg-10">
        <div class="">
            <div class="col-lg-2-no-padding padding-right-2">
                <input name="quoteBL1" type="text" class="form-control" id="quotesB1" placeholder="eg. He did not say ...">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="quoteBL2" type="text" class="form-control" id="quotesB2" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="quoteBL3" type="text" class="form-control" id="quotesB3" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2 padding-right-2">
                <input name="quoteBL4" type="text" class="form-control" id="quotesB4" placeholder="">
            </div>
            <div class="col-lg-2-no-padding padding-left-2">
                <input name="quoteBL5" type="text" class="form-control" id="quotesB5" placeholder="">
            </div>
        </div>
        <div class="fileinput fileinput-new input-group" data-provides="fileinput" id="quotesB">
            <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
            <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="quoteBLF"></span>
            <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
        </div>

        <div class="checkbox col-lg-10">
            <label>
                <input name="removeNoQuotes" type="checkbox"> Remove records without quotes
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
                <input name="removeVersionsA" type="checkbox"> A
            </label>
        </div>
        <div class="checkbox">
            <label>
                <input name="removeVersionsB" type="checkbox"> B
            </label>
        </div>
        <div class="checkbox">
            <label>
                <input name="removeVersionsC" type="checkbox"> C
            </label>
        </div>
        <div class="checkbox">
            <label>
                <input name="removeVersionsD" type="checkbox"> D
            </label>
        </div>
        <div class="checkbox">
            <label>
                <input name="removeVersionsE" type="checkbox"> E
            </label>
        </div>
        <div class="checkbox"></div>
    </div>
</div>
<!-- Remove versions -->

<!-- Other -->
<legend>Other</legend>
<div class="form-group">
    <div class="col-lg-10 margin-left-content">

        <div class="checkbox">
            <label>
                <input name="removeGarbled" type="checkbox" checked> Remove garbled text
            </label>
        </div>
        <div class="checkbox">
            <label>
                <input name="caseInsensitive" type="checkbox"> Make all matching case insensitive
            </label>
        </div>
        <div class="checkbox">
            <label>
                <input name="formatF5" type="checkbox"> Use one line output format (F5)
            </label>
        </div>
        <div class="checkbox"></div>
    </div>
</div>
<!-- Remove versions -->

<input class="btn btn-success btn-lg center-block" type="submit" name="search" value="Search" />

</fieldset>
</form>

<?php
include_once "footer.html";
?>
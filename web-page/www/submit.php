<?php
// Please define your class ID here (it will be in different parts of the script)
$CLASS_ID = "CS224W";
$ALLOWED_EXT = array("m", "java", "py", "cpp", "c", "rb", "cs");
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><?php echo $CLASS_ID; ?> Assignment Submission Form</title>

<!-- Standard reset, fonts and grids -->
<link rel="stylesheet" type="text/css" href="http://snap.stanford.edu/styles/reset-fonts-grids.css">
<!-- styles for the whole website -->
<link href="styles.css" rel="stylesheet" type="text/css" />
</head>

<body class="yui-skin-sam" id="yahoo-com">
<div id="doc" class="yui-t1">

<div id="hd">
<!-- START: header -->
<div id="header">
  <a href="http://cs.stanford.edu/~jure/"><img id="by-jure" src="http://snap.stanford.edu/images/by_jure.gif" alt="By Jure Leskovec" /></a>
  <img id="mld-logo" src="http://snap.stanford.edu/images/empty_logo.png" alt="" />
  <a href="http://www.stanford.edu/"><img id="stanford-logo" src="http://snap.stanford.edu/images/stanford.png" alt="Stanford University" /></a>
</div>
<!-- END: header -->
</div>

<!-- START: left column -->
<div id="left-column" align="left">
<br><br>
  <!--BEGIN left_column.html -->
  <center><a href="http://www.stanford.edu/"><img id="stanford-logo" src="http://snap.stanford.edu/images/seal.gif" alt="Stanford"/></a></center>
  <!--
  <ul id="links-under-menu">
    <li><a href=""></a></li>
  </ul>
  -->
  <!--END left_column.html -->
</div>
<!-- END: left column -->

<!-- START: right column -->
<div id="right-column">
<br><br>
<div style="color:#ff8b00; font-size:36px; font-weight:bold;">Assignment submission form - <?php echo $CLASS_ID; ?></div>
<p>
  This webpage is intended for electronic submissions of homework assignments.
</p>
<!--
<p>
  Please note that we only accept files in the following formats: 
  <?php foreach ($ALLOWED_EXT as $ext) echo "<b>.$ext</b> "; ?>
</p>
-->
<p>
  Enter your data in the form below and click the <i>submit</i> button to upload your
  file. Only the last version of the file you upload will be saved. 
</p>

<?php
function errmsg($msg) {
  echo "<h3>Error submitting your data</h3>\n";
  echo "<p>$msg</p>\n";
  echo "<p>Please try <a href=\"javascript:history.go(-1)\">again</a>.</p>";
}

$q = "0";
if (isset($_POST['sendfile']) && $_POST['sendfile'] == "Submit") {
  if (!isset($_POST['sunetid']) || !preg_match("/^[0-9a-zA-Z_]{3,100}$/i", $_POST['sunetid'])) {
    errmsg("The SUNetID you have entered is invalid. It should contain 3-8 letters or numbers.");
  } else if (!isset($_POST['hw']) || $_POST['hw'] < 0 || $_POST['hw'] > 5) {
    errmsg("You have not selected a valid homework number.");
  } else if (!isset($_POST['q']) || $_POST['q'] < 1 || $_POST['q'] > 7) {
    errmsg("You have not selected a valid question number.");
  } else if (!isset($_POST['class']) || !($_POST['class'] == $CLASS_ID)) {
    errmsg("Submitted class id and script class id do not match.");
  } else {
    //check for file and write it to disk...
    //error_log(var_dump($_FILES));
    $fname = sprintf("%s-hw%s-q%s", strtolower($_POST['sunetid']), $_POST['hw'], $_POST['q']);
    $uploaddir = "/lfs/snap/0/submit/upload/";
    $uploadext = pathinfo($_FILES['userfile']['name'], PATHINFO_EXTENSION);
    $uploadfname = pathinfo($_FILES['userfile']['name'], PATHINFO_FILENAME);
    $uploadfile = "${uploaddir}${fname}.${uploadext}";
	/*
    if (file_exists($uploadfile)) {
      $version = 1;
      while (file_exists(sprintf("%s%s.v%02d.pdf", $uploaddir, $fname, $version))) {
        $version++;
      }
      rename($uploadfile, sprintf("%s%s.v%02d.pdf", $uploaddir, $fname, $version));
    }
    */
    if ($_FILES['userfile']['size'] > 10000000 || $_FILES['userfile']['error'] == 2) {
      errmsg("The uploaded file is larger than 10 MB.");
    /* Removed the check since some users are having trouble with submitting
    } else if ($_FILES['userfile']['type'] != "application/pdf") {
      errmsg("Only <b>PDF</b> files are allowed.");
    */
/* no more extension checkig
    } else if (!in_array($uploadext, $ALLOWED_EXT)) {
          $msg = "The type of the uploaded file is incorrect. We only accept files in the following formats: "; 
          foreach ($ALLOWED_EXT as $ext) $msg .= ".$ext ";
	  errmsg($msg);
*/
    } else if (move_uploaded_file($_FILES['userfile']['tmp_name'], $uploadfile)) {
      echo "<h2 style=\"font-size: 200%; font-weight:bold;\">Success!</h2>\n<p>File was successfully uploaded. Thank you for your submission.</p>\n";
      echo "<h3>You uploaded</h3>\n";
      echo "<p>" . $_FILES['userfile']['name'] . "</p>\n";
      echo "<h3>We saved</h3>\n";
      echo "<p>${fname}.${uploadext}</p>\n";
      echo "<h3>Current time</h3>\n";
      echo date("M d Y @ h:i A");
      /*
      echo "<h3>File content</h3>\n";
      echo "This is the content of your file that was saved to the web server's disk:</p>\n";
      echo "<pre style=\"background: #eeeeee; font-family: consolas, courier, fixed; padding: 4px; width: 700px; height: 300px; font-size: 90%; overflow: auto;\">\n";
      if ($fh = fopen($uploadfile, "r")) {


        while (!feof($fh)) {
          $line = fgets($fh);
          echo htmlspecialchars($line);
        }
	# Processing

	fclose($fh);
      }
      echo "</pre>\n";
      */
    } else {
      error_log("Files array: " . $_FILES['userfile']['tmp_name']);
      error_log("Upload file: " . $uploadfile);
      errmsg("Something went wrong with copying your file from the temporary location.");
    }
  }
} else {
// Display the form
?>
<form enctype="multipart/form-data" action="<? echo $_SERVER['PHP_SELF']; ?>" method="post">
  <input type="hidden" name="class" value="<?php echo $CLASS_ID; ?>" />
  <input type="hidden" name="MAX_FILE_SIZE" value="10000000" />

  <h2>SUNetID</h2>
  <input type="text" size="16" maxlength="100" name="sunetid" />
  <p>If you don't have a SUNetID use 
  &lt;your_legal_last_name&gt;_&lt;your_legal_first_name&gt;, so if 
  your last name was Smith and your first name was John, you would
  use smith_john.</p>
  <h2>Homework</h2>
  <select name="hw">
    <option value="-1"></option>
    <option value="0">Homework 0</option>
    <option value="1">Homework 1</option>
    <option value="2">Homework 2</option>
    <option value="3">Homework 3</option>
    <option value="4">Homework 4</option>
    <option value="5">Project</option>
</select>

  <h2>Question</h2>
  <select name="q">
    <option value="0"></option>
    <option value="1">Question 1</option>
    <option value="2">Question 2</option>
    <option value="3">Question 3</option>
    <option value="4">Question 4</option>
    <option value="5">Proposal</option>
    <option value="6">Milestone</option>
    <option value="7">Final report</option>
</select>
  

  <h2>Your File</h2>
<!--  <p>Please note that we only accept files in the following formats:
  <?php foreach ($ALLOWED_EXT as $ext) echo "<b>.$ext</b> "; ?>
  </p>
-->
  <input type="file" name="userfile" size="55" maxlength="256"/>
  <h2>Click on the button below to submit</h2>
  <input type="submit" name="sendfile" value="Submit" size=10 /> <input type="reset" value="Reset form" />
</form>
<?php
} //diplay form
?>

</div>
<!-- END: right column -->

<div id="footer">
<!-- you can put something here -->
</div>
</div>

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl."
: "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost +
"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-342639-4");
pageTracker._trackPageview();
} catch(err) {}</script>
</body>
</html>

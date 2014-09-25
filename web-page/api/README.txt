news-search/api/index.php GET => izpise queue


news-search/api/index.php/queue_id GET => vrne json za queue
{ start_date: xxx;
  lang_whitelist: http://snap/news... 
}

news-search/api/index.php/queue_id UPDATE => posljes json
{
  app_id: url
  job_id: url
  finished: true/false
  progress: %
}
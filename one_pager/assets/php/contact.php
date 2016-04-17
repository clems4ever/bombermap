<?php
  require 'vendor/phpmailer/PHPMailerAutoload.php';

//
// config
// --------------------------------------------------
//

  $EMAIL   = 'email@example.com'; // your email address
  $NAME    = 'YOUR_NAME'; // your name
  $SUBJECT = 'Website Contact Message'; // subject line

  $HTML = '
    <html>
      <head>
        <title>' . $SUBJECT . '</title>
      </head>
      <body>
        <p><strong style="width: 80px;">Name: </strong>' . $_POST['name'] . '</p>
        <p><strong style="width: 80px;">Email: </strong>' . $_POST['email'] . '</p>
        <p><strong style="width: 80px;">Message: </strong>' . $_POST['message'] . '</p>
      </body>
    </html>
  ';

  if($_SERVER['REQUEST_METHOD'] != 'POST') {
    die('An error occurred. Please try again later.'); // no html
  } else {
    if (empty($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH']) != 'xmlhttprequest') {
      die('Only allow access via AJAX'); // no html
    } else {
      if (empty($EMAIL) || empty($NAME) || empty($SUBJECT) || empty($HTML)) {
        die(json_encode(array('type' => 'error', 'msg' => '<i class="fa fa-warning"></i> An error occurred. Please check php config field.')));
      } else {
        extract($_POST, EXTR_PREFIX_ALL, 'form');

        $mail = new PHPMailer(true);
        $mail->CharSet = 'UTF-8';
        $mail->setFrom($form_email, $form_name);
        $mail->addReplyTo($form_email, $form_name);
        $mail->addAddress($EMAIL, $NAME);
        $mail->Subject = $SUBJECT;
        $mail->msgHTML($HTML);

        if (!$mail->send()) {
          die(json_encode(array('type' => 'error', 'msg' => '<i class="fa fa-warning"></i> An error occurred. Please try again later.')));
        } else {
          die(json_encode(array('type' => 'success', 'msg' => '<i class="fa fa-check-square"></i> Your message has been sent.')));
        }
      }
    }
  }
?>
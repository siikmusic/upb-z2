<!DOCTYPE HTML PUBLIC "−//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http−equiv="Content−Type" content="text/html; charset=UTF−8">
    <title>File Crypter Example in JSP and Servlet − Java webapplication </title>
</head>
<body>
<div>
    <h3> Choose File to Encrypt/Decrypt</h3>
    <form action="upload" method="post" enctype="multipart/form-data">
        <input type="radio" id="encrypt" name="mode" value="encrypt" checked>
        <label for="encrypt">Encrypt</label>
        <input type="radio" id="decrypt" name="mode" value="decrypt">
        <label for="decrypt">Decrypt</label><br><br>

        <input type="file" name="file"/>Key (only for decryption):
        <input type="text" id="fname" name="fname"><br><br>
        <input type="submit" value="upload"/>
    </form>
</div>
</body>
</html>

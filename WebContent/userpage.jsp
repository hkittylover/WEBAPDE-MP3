<%@page import="edu.webapde.service.PhotoService"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <script src="jquery-3.2.1.min.js"></script>
        <script src="jquery-ui.min.js"></script>
        <link rel="stylesheet" runat="server" href="stylesheet.css">
        <link rel="stylesheet" href="jquery-ui.css">
        <style>
            ul.ui-autocomplete {
                width: 240px !important;
                background-color: whitesmoke;
                margin-left: 1em;
                border-style: solid;
            }
            .ui-autocomplete { 
                position: fixed; 
                cursor: default;
                z-index:30 !important;
                max-height: 100px;
                overflow-y: auto;
                /* prevent horizontal scrollbar */
                overflow-x: hidden;
                /* add padding to account for vertical scrollbar */
                padding-right: 20px;
                line-height: 2;
            }  
            .ui-menu-item {
                display: block;
                width: 240px;
                height: 30px;
                color: #424242;
            }
            .ui-menu-item-wrapper {
                height: 30px;
            }
            #advsearch{
                position:fixed;
                width:240px;
                height:25px;
                background-color:whitesmoke;
                color:cornflowerblue;
                line-height:2;
                margin-left:10px;
                z-index:1000;
                padding-left:3px;
                padding-right:17px; 
                border:1px solid #C5C5C6;
                font-size: .75em;
            }
            #advsearch:hover {
                cursor:pointer;
                text-decoration:underline;
            }
        </style>
        <script>
            var photos_cnt = 0;
            var numViewPhoto = 4;

            var isAdvancedUpload = function() {
                var div = document.createElement( 'div' );

                if( ( ( 'draggable' in div ) || ( 'ondragstart' in div && 'ondrop' in div ) ) && 'FormData' in window && 'FileReader' in window)
                    console.log("is advanced upload");
                else console.log("not advanced upload");

                return ( ( 'draggable' in div ) || ( 'ondragstart' in div && 'ondrop' in div ) ) && 'FormData' in window && 'FileReader' in window;
            }();
            var picForms = [];
            var picCount = 0;

            // applying the effect for every form

            function uploadFile (file) {
                var fileList = document.getElementById("file-list");
                console.log("FILELIST: " + fileList);

                var li = document.createElement("li"),
                    div = document.createElement("div"),
                    img,
                    reader,
                    fileInfo = document.createElement("span");

                li.setAttribute("data-file-count", picCount);
                picCount++;
                var remove = document.createElement("span");
                remove.setAttribute("id", "remove-file");
                remove.innerHTML += "<i class=\"fa fa-times\" aria-hidden=\"true\"></i>";

                li.appendChild(div);

                if (typeof FileReader !== "undefined" && (/image/i).test(file.type)) {
                    img = document.createElement("img");
                    div.appendChild(img);
                    reader = new FileReader();
                    reader.onload = (function (theImg) {
                        return function (evt) {
                            theImg.src = evt.target.result;
                        };
                    }(img));
                    reader.readAsDataURL(file);
                }

                // Present file info and append it to the list of files
                fileInfo.textContent = file.name;
                div.appendChild(fileInfo);
                div.appendChild(remove);

                fileList.appendChild(li);
            }

            function traverseFiles(droppedFiles) {
                if (typeof droppedFiles !== "undefined") {
                    var $photoInfo;
                    for(var i = 0; i < droppedFiles.length; i++) {
                        console.log("UPLOADED: " + droppedFiles[i].name);
                        console.log("\tTYPE: " + droppedFiles[i].type);
                        uploadFile(droppedFiles[i]);
                        $photoInfo = $("#info-container").clone();

                        $photoInfo.children()[0].textContent = droppedFiles[i].name;
                        $photoInfo.attr("id", "");
                        picForms.push($photoInfo);
                        console.log("picForms size: " + picForms.length);
                        console.log($photoInfo.children());

                        $photoInfo.children()[1][0].defaultValue = droppedFiles[i].name.split( '.' )[0];
                    }

                    if($("#file-list").children().length > 0) {
                        $("#drop-container").css('display', 'none');
                        $("#photo-container").css('display', 'inline-block');
                        $("#info-container").css('display', 'inline-block');

                        // change bg color of selected list
                        $($("#file-list").children()[0]).addClass("list-selected");
                        $("#info-container").replaceWith(picForms[0]);

                        // replace info container
                        $(picForms[0]).attr("id", "info-container");
                        $("#info-container").css('display', 'inline-block');
                    }
                }
            }

            function showPhoto(p) {
                var a_photo = document.createElement("a");
                a_photo.textContent = "";
                a_photo.href = "#" + p.photoId;

                var new_div_tab_photo = document.createElement("div");
                new_div_tab_photo.className = "tab_photo";

                var new_thumb = document.createElement("img");
                new_thumb.className = "thumbnail";
                new_thumb.setAttribute("src", p.filepath);

                var new_tab_title = document.createElement("div");
                new_tab_title.className = "tab_title";
                new_tab_title.textContent = p.title;

                var new_div_user = document.createElement("div");
                new_div_user.className = "tab_title tab_user";

                var a_user = document.createElement("a");
                a_user.href = "userpage?userId=" + p.user.userId;
                a_user.textContent = p.user.username;

                new_div_tab_photo.appendChild(new_thumb);
                new_div_tab_photo.appendChild(new_tab_title);
                new_div_user.appendChild(a_user);
                new_div_tab_photo.appendChild(new_div_user);

                a_photo.appendChild(new_div_tab_photo);

                $("#tab_con").append(a_photo);
                $(new_div_tab_photo).show(500);
            }

            function getPhotos(){
                photoList = ${photoList};
                var limit = photos_cnt + numViewPhoto;
                for(i = photos_cnt; i < photoList.length && i < limit; i++) {
                    showPhoto(photoList[i]);
                    photos_cnt++;
                }

                if (photos_cnt >= photoList.length) {
                    $("#viewmore").hide();
                }
            }

            function findPhoto(pList, id) {
                for(i = 0; i < pList.length; i++) {
                    if(pList[i].photoId == id) {
                        console.log(pList[i]);
                        return pList[i];
                    }
                    console.log("Hello");
                }
                return null;
            }

            function showImgModal(p) {

                console.log("show the modal thing");
                //console.log($(this).data());

                //get value of id from the child div (which is id div)
                var modal_thing = document.getElementById("myModal");
                var modal_img = document.getElementById("modalimg");
                var modal_caption = document.getElementById("caption");
                var modal_uploader = document.getElementById("imguploader");
                var modal_album = document.getElementById("imgalbum");
                var modal_description = document.getElementById("imgdescription");

                $(modal_album).empty();
                $(modal_img).empty();
                $(modal_uploader).empty();
                $(modal_description).empty();

                var a_user = document.createElement("a");
                var a_album = document.createElement("a");

                document.title = p.title + " | Photos | Oink";
                modal_caption.innerHTML = p.title;
                modal_img.src = p.filepath;
                modal_description.innerHTML = p.description;
                a_user.href = "userpage?userId=" + p.user.userId;
                a_user.textContent = p.user.username;
                modal_uploader.appendChild(a_user);

                $("#imgcontainer2").empty();
                var new_img_tag = [];
                for(i = 0; i < p.tags.length; i++) {
                    new_img_tag.push(document.createElement("div"));
                    new_img_tag[i].className = "imgtag";
                    new_img_tag[i].textContent = "#" + p.tags[i].tagname;
                    $("#imgcontainer2").append(new_img_tag[i]);
                }
                modal_thing.style.display = "table";
                $("#addtag").hide();
                $("#adduser").hide();
                $("#imgusercontainer").hide();
            }

            function showMyImgModal(p) {

                console.log("show the modal thing");
                //console.log($(this).data());

                //get value of id from the child div (which is id div)
                var modal_thing = document.getElementById("myModal");
                var modal_img = document.getElementById("modalimg");
                var modal_caption = document.getElementById("caption");
                var modal_uploader = document.getElementById("imguploader");
                var modal_album = document.getElementById("imgalbum");
                var modal_description = document.getElementById("imgdescription");

                $(modal_album).empty();
                $(modal_img).empty();
                $(modal_uploader).empty();
                $(modal_description).empty();

                var a_user = document.createElement("a");
                var a_album = document.createElement("a");

                document.title = p.title + " | Photos | Oink";
                modal_caption.innerHTML = p.title;
                modal_img.src = p.filepath;
                modal_description.innerHTML = p.description;
                a_user.href = "userpage?userId=" + p.user.userId;
                a_user.textContent = p.user.username;
                modal_uploader.appendChild(a_user);

                $("#imgcontainer2").empty();
                var new_img_tag = [];
                for(i = 0; i < p.tags.length; i++) {
                    new_img_tag.push(document.createElement("div"));
                    new_img_tag[i].className = "imgtag";
                    new_img_tag[i].innerHTML = "#" + p.tags[i].tagname + "<span class=\"remove-tag\" style=\"margin-left: 8px;\"><i class=\"fa fa-times\" id=\"fa-timestag\" aria-hidden=\"true\" data-photoId = \"" + p.photoId + "\" data-tagname=\"" + p.tags[i].tagname + "\"></i></span>";
                    new_img_tag[i].setAttribute("data-tagname", p.tags[i].tagname);
                    new_img_tag[i].setAttribute("data-photoId", p.photoId);
                    $("#imgcontainer2").append(new_img_tag[i]);
                }
                modal_thing.style.display = "table";
                $("#addtag").empty();
                $("#addtag").append("<i  id=\"fa-addtag\" class=\"fa fa-plus\" aria-hidden=\"true\"></i><span id=\"add-tag\"></span>");
                $("#addtag").show();

                if(p.privacy == 'private') {
                    $("#imgusercontainer").show();
                    $("#imgusercontainer2").empty();
                    var new_img_user = [];
                    for(i = 0; i < p.allowedUsers.length; i++) {
                        new_img_user.push(document.createElement("div"));
                        new_img_user[i].className = "imgtag";
                        new_img_user[i].textContent = p.allowedUsers[i].username;
                        $("#imgusercontainer2").append(new_img_user[i]);
                    }
                    modal_thing.style.display = "table";
                    $("#adduser").empty();
                    $("#adduser").append("<i  id=\"fa-adduser\" class=\"fa fa-plus\" aria-hidden=\"true\"></i><span id=\"add-user\"></span>");
                    $("#adduser").show();
                }
                else{
                    $("#adduser").hide();
                    $("#imgusercontainer").hide();
                }
            }

            function closeModal() {
                var modal = document.getElementById("myModal");
                var modallog = document.getElementById("modal-login");
                var modalap = document.getElementById("modal-add-photo");
                var modalreg = document.getElementById("modal-reg");
                var modals = document.getElementById("modal-sear");

                modal.style.display = 'none';
                modallog.style.display = 'none';
                modalap.style.display = 'none';
                modalreg.style.display = 'none';
				modals.style.display = 'none';
                
                document.title = "${username}";

                history.replaceState('', document.title, "http://localhost:8080/WEBAPDE-MP3/userpage?userId=${sessionScope.sUserId}");
            }

            function myFocusFunction() {
                $("#search-results").css("display", "block");  
            }

            function myBlurFunction() {
            	console.log("hey");
                $("#search-results").css("display", "none");
            }

            function openAdvsearch(){
            	var modal_thing = document.getElementById("modal-sear");

                modal_thing.style.display = "table";
            }

            $(document).ready(function(){
                history.replaceState('', document.title, "http://localhost:8080/WEBAPDE-MP3/userpage?userId=${sessionScope.sUserId}");
                document.title = "${username} | Oink";
                var role = "${sessionScope.role}";
                var action = "${action}";
                console.log(role);
                console.log("ACTION: ${action}");

                // adjust UI depending on the role
                if(role == "user") {
                    console.log("I AM AN USER")
                    console.log("${sessionScope.sUsername} ");
                    getPhotos();
                    var username = "${sessionScope.sUsername} ";
                    var description = "${sessionScope.sDescription} ";
                    // you can hide the log in and register and show logout and account

                    // you can show the section for private photos
                }
                else {
                    // check for action failures
                    if(action == "login") {
                        // if login failed
                        if("${ERROR}" == "failed") {
                            console.log("I AM STILL A GUEST")
                            console.log("LOGIN FAILED");

                            // continue to show the modal of login but add a div inside to state that the username or password is incorrect

                        }
                    }

                    else if(action == "register") {
                        // if register failed
                        if("${ERROR}" == "failed") {
                            console.log("I AM STILL A GUEST")
                            console.log("REGISTER FAILED");

                            // continue to show the modal of register but add a div inside to state that the username or password is incorrect
                        }
                    }
                    var username = "${username} ";
                    var description = "${description} ";
                    console.log("USERNAME SESSION: " + username);
                    getPhotos();
                }

                if("${sessionScope.sUsername}" == "${username}") {
                    document.getElementById("pagestatus").textContent = "My";
                }
                else {
                    document.getElementById("pagestatus").textContent = "${username}'s";
                    $("#add-photo").hide();
                }

                $("#viewmore").click(function (e) {
                    e.preventDefault();

                    getPhotos();
                });


                $(document).on("mouseenter", ".imgtag#addtag", function(){
                    var $img_span = $(this).find("span");
                    var $img_i = ($img_span).find("i");
                    var $img_i_plus = ($img_span).find("i.fa-plus#addtag");

                    console.log("INPUT LENGTH " + $(this).find("input").length);
                    if($img_span.attr("id") == "add-tag" && $(this).find("input").length == 0) {
                        $img_span.stop().hide();
                        $img_span.empty();
                        $img_span.append("<span>Add tag</span>");
                        $img_span.css({marginLeft:"10px"});
                        $img_span.stop().show(250);
                    } else if($img_i.length == 0 || $img_i_plus.length == 1) {
                        $img_span.stop().hide();
                        $img_span.append("<i class=\"fa fa-times\" id=\"fa-timestag\" aria-hidden=\"true\"></i>");
                        $img_span.css({marginLeft:"10px"});
                        $img_span.stop().show(250);
                    }
                });

                $(document).on("mouseenter", ".imgtag#adduser", function(){
                    var $img_span = $(this).find("span");
                    var $img_i = ($img_span).find("i");
                    var $img_i_plus = ($img_span).find("i.fa-plus#fa-adduser");

                    console.log("INPUT LENGTH " + $(this).find("input").length);
                    if($img_span.attr("id") == "add-user" && $(this).find("input").length == 0) {
                        $img_span.stop().hide();
                        $img_span.empty();
                        $img_span.append("<span>Add user</span>");
                        $img_span.css({marginLeft:"10px"});
                        $img_span.stop().show(250);
                    } else if($img_i.length == 0 || $img_i_plus.length == 1) {
                        $img_span.stop().hide();
                        $img_span.append("<i class=\"fa fa-times\" id=\"fa-timesuser\" aria-hidden=\"true\"></i>");
                        $img_span.css({marginLeft:"10px"});
                        $img_span.stop().show(250);
                    }
                });

                $(document).on("mouseleave", ".imgtag#addtag", function(){
                    var $img_span = $(this).find("span");
                    $img_span.stop().hide(250, function(){
                        $img_span.text("");
                        $img_span.css({marginLeft:"0px"});
                    });
                });

                $(document).on("mouseleave", ".imgtag#adduser", function(){
                    var $img_span = $(this).find("span");
                    $img_span.stop().hide(250, function(){
                        $img_span.text("");
                        $img_span.css({marginLeft:"0px"});
                    });
                });

                $(document).on("click", ".fa-times#fa-timestag", function() {
                    var tagname = $(this).attr("data-tagname");
                    var photoId = $(this).attr("data-photoId");
                    if(confirm("Do you want to delete the \"" + tagname + "\" tag?")) {
                        $.ajax({
                            "url" : "deletetag",
                            "method" : "POST",
                            "success" : function(result) {
                                console.log(result);
                                if(result == "true") {
                                    $("div.imgtag[data-tagname=" + tagname + "]").remove();
                                }
                                else {
                                    alert("Delete Tag Failed");
                                }

                            },
                            "data" : {
                                "photoId" : photoId,
                                "tagname" : tagname
                            }
                        });
                    }
                });

                $(document).on("click", ".imgtag#addtag", function() {
                    var $img_span = $(this).find("span");
                    var new_textarea = document.createElement("input");

                    new_textarea.type="text";
                    new_textarea.placeholder="Enter tag";
                    new_textarea.size="8";
                    new_textarea.className = "inputtag";
                    new_textarea.setAttribute("id", "inputaddtag");

                    //$(this).css("pointerEvents", "none");
                    if($(this).find("input").length == 0) {
                        console.log("ADD INPUT FIELD");
                        $(this).append(new_textarea);
                        $($img_span).remove( ":contains('Add tag')" );
                    }
                    //alert("ADD TAG");
                });
                

                $(document).on("click", ".imgtag#adduser", function() {
                    var $img_span = $(this).find("span");
                    var new_textarea = document.createElement("input");

                    new_textarea.type="text";
                    new_textarea.placeholder="Enter user";
                    new_textarea.size="8";
                    new_textarea.className = "inputtag";
                    new_textarea.setAttribute("id", "inputadduser");

                    //$(this).css("pointerEvents", "none");
                    if($(this).find("input").length == 0) {
                        console.log("ADD INPUT FIELD");
                        $(this).append(new_textarea);
                        $($img_span).remove( ":contains('Add user')" );
                    }
                    //alert("ADD TAG");
                });

                $(document).on("click", "#fa-addtag", function(e) {
                    e.stopPropagation();
                    if($("#addtag").find("input").length >= 1) {
                        //console.log("tag?id=" + parseInt(window.location.hash.slice(1)) + "&tag=" + $(".inputtag#inputadduser").val());
                        var tag = $(".inputtag#inputaddtag").val();
                        var ajx = $.post("tag" + window.location.search + "&tag=" + tag, function(obj) {
                            if(obj == "true") {
                                var img_tag = document.createElement("div");
                                img_tag.className += "imgtag";
                                img_tag.textContent = "#" + tag;
                                $("#imgcontainer2").append(img_tag);
                            }
                            else {
                                console.log("ERROR ADD TAG");
                            }
                        });

                        $("#addtag").empty();
                        //alert("ADD TAG TO DB");
                        $("#addtag").append("<i class=\"fa fa-plus\" aria-hidden=\"true\"></i><span id=\"add-tag\"></span>");

                    }
                });

                $(document).on("click", "#fa-adduser", function(e) {
                    e.stopPropagation();
                    if($("#adduser").find("input").length >= 1) {
                        //console.log("tag?id=" + parseInt(window.location.hash.slice(1)) + "&tag=" + $(".inputtag").val());
                        var user = $(".inputtag#inputadduser").val();
                        var ajx = $.post("share" + window.location.search + "&user=" + user, function(obj) {
                            if(obj == "true") {
                                var img_tag = document.createElement("div");
                                img_tag.className += "imgtag";
                                img_tag.textContent = user;
                                $("#imgusercontainer2").append(img_tag);
                            }
                            else {
                                console.log("ERROR ADD USER");
                            }
                        });

                        $("#adduser").empty();
                        //alert("ADD TAG TO DB");
                        $("#adduser").append("<i class=\"fa fa-plus\" aria-hidden=\"true\"></i><span id=\"add-user\"></span>");

                    }
                });

                // on hash change, show photo
                $(window).on('hashchange',function(e){
                    //e.preventDefault();
                    if(window.location.hash.slice(1) != "") {
                        $.ajax({
                            "url" : "photodetails",
                            "method" : "POST",
                            "success" : function(result) {
                                console.log(result);
                                history.replaceState('', document.title, "http://localhost:8080/WEBAPDE-MP3/photo?id=" + window.location.hash.slice(1));
                                if(result != null) {
                                    console.log(result);
                                    if(result.user.username == "${sessionScope.sUsername}")
                                        showMyImgModal(result);
                                    else
                                        showImgModal(result);
                                }
                                else {
                                    // no access
                                    showErrModal();
                                    history.replaceState('', document.title, "http://localhost:8080/WEBAPDE-MP3/photo?id=" + window.location.hash.slice(1));
                                }

                            },
                            "data" : {
                                "id" : window.location.hash.slice(1)
                            }
                        });
                    }
                });



                // close modal on x button
                $(document).on("click", ".close", function(){
                    console.log("close modal");
                    window.location.hash = "";
                    document.title = "${username} | Oink";

                    closeModal();
                });
                
                $('#advsearch').click(function(event){
                	console.log("advsearch");
                    event.preventDefault();
                	var modal_thing = document.getElementById("modal-sear");

                    modal_thing.style.display = "table";
                });

                // close modal after any click outside modal
                $(window).on("click", function() {


                    var modal = document.getElementById("myModal");
                    var modallog = document.getElementById("modal-login");
                    var modalap = document.getElementById("modal-add-photo");

                    var modalc = document.getElementById("modal-container");
                    var modalc2 = document.getElementById("modal-container-2");
                    var modalclog = document.getElementById("modal-login-container");
                    var modalc2log = document.getElementById("modal-login-container-2");
                    var modaladdphoto = document.getElementById("modal-add-photo-container");
                    var modaladdphoto2 = document.getElementById("modal-add-photo-container-2");
                    var modalcreg = document.getElementById("modal-reg-container");
                    var modalcreg2 = document.getElementById("modal-reg-container-2");
                    var modals = document.getElementById("modal-sear");

                    if(event.target == modals || event.target == modalc2 || event.target == modalc || event.target == modallog || event.target == modalclog || event.target == modalc2log || event.target == modaladdphoto || event.target == modaladdphoto2 || event.target == modalcreg || event.target == modalcreg2) {
                        closeModal();
                        window.location.hash = "";
                        document.title = "${username} | Oink";
                    }

                    console.log("onclick: " + event.target);
                });



                // Add photo
                $('#add-photo').click(function(){
                    console.log("add photo event");
                    event.preventDefault();
                    var modallog = document.getElementById("modal-add-photo");
                    modallog.style.display = 'table';
                });



                // Add image file
                $( '.pic-input' ).each( function() {
                    var $input   = $( this ),

                        $label   = $input.next( 'label' ),
                        labelVal = $label.html();
                    console.log('pic-input each');

                    $input.on( 'change', function( e ) {
                        console.log('pic-input change');
                        var fileName = '';
                        console.log(this.files);
                        console.log(e.target.value);
                        console.log(e.target.value.split( '\\' ).pop());
                        fileName = e.target.value.split( '\\' ).pop();

                        if( fileName ) {
                            $label.find( 'span' ).html( "<i class=\"fa fa-check-circle\" aria-hidden=\"true\"></i> " + fileName );

                            //$label.css('color', 'gray');
                        }
                        else {
                            $label.html( labelVal );

                        }
                    });

                    // Firefox bug fix
                    $input
                        .on( 'focus', function(){ $input.addClass( 'has-focus' ); })
                        .on( 'blur', function(){ $input.removeClass( 'has-focus' ); });
                });



                // Add allowed users
                $( '.pic-security').each( function() {
                    var $input   = $( this ),
                        $label   = $input.next( 'label' ),
                        labelVal = $label.html();
                    console.log('pic-security each');


                    var modal = document.getElementById("pic-allowed-user");

                    $input.on( 'change', function( e ) {

                        console.log('pic-security change');
                        if(labelVal == "Public") {
                            console.log("Public radio button selected");
                            modal.style.display = 'none';
                        } else if(labelVal == "Private") {
                            console.log("Private radio button selected");
                            modal.style.display = 'block';
                        }

                    });
                })

                $('a.hlink').click(function(e){
                    //e.preventDefault();
                    console.log(event.target.text)
                    event.preventDefault(); 
                    var hlinklist = document.getElementsByClassName("hlink");
                    for(i = 0; i < hlinklist.length; i++) {
                        if(event.target.text == hlinklist[i].text && hlinklist[i].text == "Login") {
                            var modallog = document.getElementById("modal-login");
                            modallog.style.display = 'table';
                        }
                        if(event.target.text == hlinklist[i].text && hlinklist[i].text == "Register") {
                            var modalreg = document.getElementById("modal-reg");
                            modalreg.style.display = 'table';
                        }
                        if(event.target.text == hlinklist[i].text && hlinklist[i].text == "Logout") {
                            window.location.href="logout";
                        }
                        if(event.target.text == hlinklist[i].text && hlinklist[i].text == "${sessionScope.sUsername}") {
                            window.location.href="userpage?userId=${sessionScope.sUserId}";
                        }
                    }

                    return false; 
                });

                $('#form-upload').submit(function(event){

                    console.log("SUBMIT LOGIN");
                    console.log($(this).find("input"));

                    var $p_errors = $(this).find("p");
                    var $input_title = $(this).find("input")[0];

                    var b = true;

                    if($input_title.value.length > 0) {
                        console.log("upload success");
                        $($input_title).css('box-shadow', 'none');
                        $($p_errors[0]).css('display', 'none');
                    }
                    else {
                        console.log("upload failed");
                        event.preventDefault();
                        $($p_errors[0]).css('display', 'block');
                        $($input_title).css('box-shadow', '0 0 0 1pt rgba(252, 45, 45, 0.8)');
                        b = false;
                    }
                    return b;
                });

                $('#form-reg').submit(function(event){

                    console.log("SUBMIT REGISTER");
                    console.log($(this).find("input"));

                    var user_regex = /^[0-9a-zA-Z]{3,}$/; // at least 3 characters alphanumeric
                    var pass_regex = /^[0-9a-zA-Z]{8,}$/; // at least 8 characters alphanumeric

                    var $p_errors = $(this).find("p");
                    var $input_user = $(this).find("input")[0];
                    var $input_pass = $(this).find("input")[1];
                    var $input_pass_c = $(this).find("input")[2];
                    var $input_desc = $(this).find("textarea")[0];

                    var b = true;

                    $($p_errors[0]).css('display', 'none');
                    $($p_errors[1]).css('display', 'none');
                    $($p_errors[2]).css('display', 'none');
                    $($p_errors[3]).css('display', 'none');

                    if(user_regex.test($input_user.value)) {
                        console.log("username 3 char alphanumeric pass");
                        $($input_user).css('box-shadow', 'none');
                        $($p_errors[0]).css('display', 'none');
                    } else {
                        console.log("username 3 char alphanumeric fail");
                        event.preventDefault(); 
                        $($p_errors[0]).css('display', 'block');
                        $($input_user).css('box-shadow', '0 0 0 1pt rgba(252, 45, 45, 0.8)');
                        b = false;
                    }

                    if($input_pass.value.length >= 8) {
                        console.log("password 8 char pass");
                        $($input_pass).css('box-shadow', 'none');
                        $($p_errors[1]).css('display', 'nones');
                        if(($input_pass.value == $input_pass_c.value)) {
                            console.log("password match");
                            $($p_errors[2]).css('display', 'none');
                            $($input_pass).css('box-shadow', 'none');
                            $($input_pass_c).css('box-shadow', 'none');
                        } else {
                            console.log("passwords do not match");
                            event.preventDefault();
                            $($p_errors[2]).css('display', 'block');
                            $($input_pass).css('box-shadow', '0 0 0 1pt rgba(252, 45, 45, 0.8)');
                            $($input_pass_c).css('box-shadow', '0 0 0 1pt rgba(252, 45, 45, 0.8)');
                            b = false;
                        }
                    } else {
                        console.log("password 8 char fail");
                        event.preventDefault(); 
                        $($input_pass).css('box-shadow', '0 0 0 1pt rgba(252, 45, 45, 0.8)');
                        $($input_pass_c).css('box-shadow', '0 0 0 1pt rgba(252, 45, 45, 0.8)');
                        $($p_errors[1]).css('display', 'block');
                        b = false;
                    }
                    var ajx = $.ajax({type: "get", url : "hasusername?username=" + $input_user.value, async: false});
                    if(ajx.responseText == "true") {
                        console.log("has username");
                        event.preventDefault();
                        $($input_user).css('box-shadow', '0 0 0 1pt rgba(252, 45, 45, 0.8)');
                        $($p_errors[3]).css('display', 'block');
                        b = false;
                    }
                    else {
                        console.log("register success");
                        $($input_user).css('box-shadow', 'none');
                        $($p_errors[3]).css('display', 'none');
                    }
                    return b;
                });

                $('#form-login').submit(function(event){

                    console.log("SUBMIT LOGIN");
                    console.log($(this).find("input"));

                    var $p_errors = $(this).find("p");
                    var $input_user = $(this).find("input")[0];
                    var $input_pass = $(this).find("input")[1];

                    var b = true;

                    var ajx = $.ajax({type: "get", url : "verifyaccount?username=" + $input_user.value + "&password=" + $input_pass.value, async: false});
                    console.log("ajxtext"+ ajxtext);
                    console.log(ajx.responseText);
                    if(ajx.responseText == "true") {
                        console.log("login success");
                        $($input_user).css('box-shadow', 'none');
                        $($input_pass).css('box-shadow', 'none');
                        $($p_errors[0]).css('display', 'none');
                    }
                    else {
                        console.log("login failed");
                        event.preventDefault();
                        $($p_errors[0]).css('display', 'block');
                        $($input_user).css('box-shadow', '0 0 0 1pt rgba(252, 45, 45, 0.8)');
                        $($input_pass).css('box-shadow', '0 0 0 1pt rgba(252, 45, 45, 0.8)');
                        b = false;
                    }
                    return b;
                });

                $( "#tagsearch" ).autocomplete({
                    source: ${tagnames},
                                               open : function(){
                    $(".ui-autocomplete:visible").css({top:"+=25",left:"+=10"});
                },
                    appendTo : "#search-results"
            });
                $( "#keyword1search" ).autocomplete({
                    source: ${tagnames},
                    open : function(){
                    $(".ui-autocomplete:visible").css({top:"+=0",left:"+=10"});
                }
            });
                $( "#keyword2search" ).autocomplete({
                    source: ${tagnames},
                    open : function(){
                    $(".ui-autocomplete:visible").css({top:"+=0",left:"+=10"});
                }
            });
                $( "#inputadduser" ).autocomplete({
                    source: ${usernames},
                    open : function(){
                    $(".ui-autocomplete:visible").css({top:"+=0",left:"+=10"});
                }
            });
                
                $("#tagsearch").focusin(function(){
                    myFocusFunction();
                });
                    
                $("#tagsearch").focusout(function(){
                	setTimeout(myBlurFunction, 1000);
                });

            $(document).on("click", "#file-list li", function(){
                var listChildren = $("#file-list").children();
                var index = parseInt($(event.target).closest('li').attr("data-file-count"));
                for(i = 0; i < listChildren.length; i++) {
                    if($(listChildren[i]).hasClass("list-selected")) {
                        $(listChildren[i]).removeClass("list-selected");
                    }
                }

                // change bg color of selected list
                $(event.target).closest('li').addClass("list-selected");
                $("#info-container").replaceWith(picForms[index]);

                // replace info container
                $(picForms[index]).attr("id", "info-container");
                $("#info-container").css('display', 'inline-block');
            });


            $( '.box' ).each( function() {
                console.log(".box each yo");
                var $form        = $( this ),
                    $input       = $form.find( 'input[type="file"]' ),
                    $label       = $form.find( 'label' ),
                    droppedFiles = false,
                    showFiles    = function( files ) {
                        //$label.text( files.length > 1 ? ( $input.attr( 'data-multiple-caption' ) || '' ).replace( '{count}', files.length ) : files[ 0 ].name );
                    };

                // letting the server side to know we are going to make an Ajax request
                $form.append( '<input type="hidden" name="ajax" value="1" />' );

                // when the upload button is clicked
                $input.on( 'change', function( e ) {
                    var fileList = document.getElementById("file-list");
                    droppedFiles = this.files;

                    console.log("BEFORE FILE LIST LENGTH: " + $("#file-list").children().length);
                    showFiles( e.target.files );

                    // DEBUGGING ::: information about the files uploaded
                    console.log(this.files);
                    console.log(e.target.value);
                    console.log(e.target.value.split( '\\' ).pop());

                    // Display uploaded files in photoContainer
                    traverseFiles(droppedFiles);
                    console.log("AFTER FILE LIST LENGTH: " + $("#file-list").children().length);

                    // If there at least one photo uploaded, remove the drop container
                    if($("#file-list").children().length > 0) {
                        $("#drop-container").css('display', 'none');
                        $("#photo-container").css('display', 'inline-block');
                        $("#info-container").css('display', 'inline-block');
                    }
                });


                // drag&drop files if the feature is available
                if( isAdvancedUpload ) {
                    $form
                        .addClass( 'has-advanced-upload' ) // letting the CSS part to know drag&drop is supported by the browser
                        .on( 'drag dragstart dragend dragover dragenter dragleave drop', function( e ) {
                        console.log("onadvancedupload");
                        e.preventDefault();
                        e.stopPropagation();
                    })
                        .on( 'dragover dragenter', function() //
                            {
                        $form.addClass( 'is-dragover' );
                    })
                        .on( 'dragleave dragend drop', function()
                            {
                        $form.removeClass( 'is-dragover' );
                    })
                        .on( 'drop', function( e )
                            {
                        console.log("dropped files");
                        droppedFiles = e.originalEvent.dataTransfer.files; // the files that were dropped
                        showFiles( droppedFiles );

                        console.log(droppedFiles);
                        traverseFiles(droppedFiles);
                        // file names

                    });
                }

            });
            });


        </script>
    </head>

    <body>
        <div id="header">
            <div id="hleft">
                <a id="hlogo" href="homepage">OINK</a>
            </div>

            <div id="hright">
                <form action="search" method="get" class="index-search-form" name="">
                    <input name="keyword" type="text"
                           placeholder="What are you looking for?" id="tagsearch">
                    <div id="search-results" style="position:fixed;display:none;"><div id="advsearch">Advanced Search</div></div>
                    <input type="hidden" value="NONE"> </input>
                <button class="" type="submit">
                    <i class="fa fa-search" aria-hidden="true"></i>
                </button>
                </form>
            <c:choose>
                <c:when test="${sessionScope.role == 'user'}">
                    <a class="hlink" id="profile" href="userpage?userId=${sessionScope.sUserId}">${sessionScope.sUsername}</a>
                    <a class="hlink" id="hlink_logout" href="logout">Logout</a>
                </c:when>
                <c:otherwise>
                    <a class="hlink" id="hlink_login" href="login">Login</a> 
                    <a class="hlink" id="hlink_reg" href="register">Register</a> 
                </c:otherwise>
            </c:choose>
        </div>
        </div>

    <div id="bodypanel">
        <div id="usermaindiv">
            <div id="userinfo">
                <div id="username-container">
                    <h3>${username }</h3>
                </div>
                <div id="user-desc-container">
                    <p>${description }</p>
                    <!-- <p>She got a body like an hourglass but I can give it to you all the time. She got a booty like a Cadillac but I can send you into overdrive.</p> -->
                </div>
            </div>
        </div>


        <div id="usercontentnav">
            <ul>
                <li><a class="navlink" id="nav-current"><span id="pagestatus">My</span> Photos</a></li>
            </ul>
        </div>

        <div id="usercontent">
            <div class="tab_con" id="tab_con">
                <a href="#" id="add-photo"><div class="tab_photo"><img class="thumbnail" src="img/sprites/add.png"/><div class="tab_title" id="tab-add-photo">Add photo</div></div></a>
            </div>
        </div>


        <div id="myModal" class="modal">

            <span class="close" >&times;</span>
            <div id="modal-container">
                <div id="modal-container-2">
                    <div id="modal-photo-container">
                        <div id="modal-photo-center">
                            <img class="modal-content" id="modalimg" />
                        </div>
                    </div>    

                    <div id="modal-info-container">
                        <div id="caption"></div>
                        <div id="imguploader"></div>
                        <div id="imgalbum"></div>
                        <div id="imgdescription"></div>
                        <div id="imgcontainer">
                            Tags:
                            <div id="imgcontainer2">
                            </div>
                            <div class="imgtag" id="addtag"><i id="fa-addtag" class="fa fa-plus" aria-hidden="true"></i><span id="add-tag"></span></div>
                        </div>
                        <div id="imgusercontainer">
                            Allowed users:
                            <div id="imgusercontainer2">
                            </div>
                            <div class="imgtag" id="adduser"><i id="fa-adduser" class="fa fa-plus" aria-hidden="true"></i><span id="add-user"></span></div>
                        </div>

                    </div>
                </div>
            </div>
        </div>


        <div id="modal-login" class="modal">

            <span class="close" >&times;</span>
            <div id="modal-login-container">
                <div id="modal-login-container-2">
                    <div id="modal-login-content">
                        <h3>Log in</h3>
                        <form action="login" method="get" id="form-login">
                            <input type="text" name="username" placeholder="Username"><br>
                            <input type="password" name="password" placeholder="Password"><br>
                            <p class="form-error">Username or password is incorrect.</p>
                            <input type="checkbox" name="remember" value="remember" /> Remember Me<br>
                            <input type="submit" value="Log in">
                        </form>
                    </div>
                </div>
            </div>

        </div>

        <div id="modal-reg" class="modal">

            <span class="close" >&times;</span>
            <div id="modal-reg-container">
                <div id="modal-reg-container-2">
                    <div id="modal-reg-content">
                        <h3>Register</h3>
                        <form action="register" method="post" id="form-reg">
                            <input type="text" name="username" placeholder="Username"><br>
                            <input type="password" name="password" placeholder="Password"><br>
                            <input type="password" name="confirmpassword" placeholder="Confirm Password"><br>
                            <textarea rows="3" name="description" placeholder="Short description about yourself (optional)"></textarea><br>
                            <p class="form-error">Username should be at least 3 alphanumeric characters.</p>
                            <p class="form-error">Password should be at least 8 characters.</p>
                            <p class="form-error">Passwords do not match.</p>
                            <p class="form-error" id="usernameexist">Username already exist.</p>
                            <input type="submit" value="Register" id="submit-register">
                        </form>
                    </div>
                </div>
            </div>

        </div>
        
        <div id="modal-sear" class="modal">
            <span class="close" >&times;</span>
            <div id="modal-sear-container">
                <div id="modal-sear-container-2">
                    <div id="modal-sear-content">
                        <h3>Advanced Search</h3>
                        <form action="search" method="get" id="form-sear">
                            <label>Search for</label><br>
                            <input type="text" name="keyword" placeholder="Enter keyword 1" id="keyword1search"><br>
                            <select name="action">
                                <option value="AND">AND</option>
                                <option value="OR">OR</option>
                            </select><br>
                            <input type="text" name="keyword2" placeholder="Enter keyword 2" id="keyword2search"><br>

                            <input type="submit" value="Search" id="submit-search">
                        </form>
                    </div>
                </div>
            </div>
        </div>


        <div id="modal-add-photo" class="modal">

            <span class="close" >&times;</span>
            <div id="modal-add-photo-container">
                <div id="modal-add-photo-container-2">
                    <div id="inline-container" class="box">

                        <div id="photo-container">
                            <ul id="file-list">

                            </ul>
                        </div>
                        <div id="info-container">
                            <h1>H1</h1>
                            <form action="" id="form-upload">
                                <input type="text" name="title" placeholder="Title"><br>
                                <input type="text" name="tag" placeholder="Tags (comma-separated, ex. tag1, tag2)"><br>
                                <textarea rows="3" name="description" placeholder="Short description about the photo"></textarea><br>
                                <ul class="switch-ul">
                                    <label class="switch">
                                        <input type="checkbox" name="selector" class="pic-security" id="toggle-private">
                                        <span class="slider round"></span>
                                        <span class="labeltext">Set to private</span>
                                    </label>
                                </ul>
                                <input type="text" name="tag" placeholder="Allowed users (ex. user1, user2)" id="pic-allowed-user"><br>

                            </form>
                        </div>
                        <div id="drop-container">
                            <p id="drop-area">
                            <h2><span class="drop-instructions">Drag your image here!</span></h2>
                            </p>

                        <form method="post" id="form-upload" name="form-upload" >
                            <label class="upload" for="files-upload" id="">Or upload a file</label>
                            <input id="files-upload" type="file" data-multiple-caption="{count} files selected" accept=".jpg, .png, .tiff" class="pic-input" multiple >
                        </form>
                    </div>

                </div>
            </div>
        </div>

    </div>

    <div id="footer">
        <div id="viewmore">
            View More...
        </div>
    </div>

    </div>

</body>
</html>
//
// config file
// --------------------------------------------------
// customize the template function via this file
//

//
// overlay
// --------------------------------------------------
//
// overlay color doesnt mean home section bckground color
// overlay will enable when we choose use `image bg` / `slideshow bg` / 'video' and 'youtube'
//

var _site_bg_overlay_color =                      'rgba(39, 49, 64, 0.7)'; // overlay color, rgba format
var _site_bg_overlay_disable =                    false; // [true, false] - force disable overlay, sometime we dont need it, disable by this variable

//
// background
// --------------------------------------------------
//
// choose background version for both desktop and mobile :)
//

// for desktop
var _bg_style_desktop =                           11;
                                                    // 0 = flat color
                                                    // 1 = flat color     (mp3 audio) - audio place at /audio/audio.mp3
                                                    // 2 = image
                                                    // 3 = image          (mp3 audio) - audio place at /audio/audio.mp3
                                                    // 4 = slideshow
                                                    // 5 = slideshow      (mp3 audio) - audio place at /audio/audio.mp3
                                                    // 6 = slideshow      (kenburn)
                                                    // 7 = slideshow      (kenburn, mp3 audio) - audio place at /audio/audio.mp3
                                                    // 8 = html5 video    (mute) - video file place at /video/video.mp4
                                                    // 9 = html5 video    (video audio)
                                                    // 10 = html5 video   (mp3 audio, audio place at /audio/audio.mp3)
                                                    // 11 = youtube video (mute)
                                                    // 12 = youtube video (video audio)
                                                    // 13 = youtube video (youtube + mp3 audio) - audio place at /audio/audio.mp3

// for mobile
var _bg_style_mobile =                            2;
                                                    // 0 = flat color
                                                    // 1 = flat color (mp3 audio) - audio place at /audio/audio.mp3
                                                    // 2 = image
                                                    // 3 = image      (mp3 audio) - audio place at /audio/audio.mp3
                                                    // 4 = slideshow
                                                    // 5 = slideshow  (mp3 audio, audio place at /audio/audio.mp3)
                                                    // 6 = slideshow  (kenburn)
                                                    // 7 = slideshow  (kenburn, mp3 audio) - audio place at /audio/audio.mp3

// if _bg_style == 4 - 7 (slideshow)
var _bg_slideshow_image_amount =                  2; // slideshow image amount
var _bg_slideshow_duration =                      9000; // millisecond

// if _bg_style_desktop == 11 - 13 (youtube video)
var _bg_video_youtube_url =                       'jbghA1CQAF8'; // youtube video url id - https://www.youtube.com/watch?v=BIaBSGgCzCc
var _bg_video_youtube_quality =                   'hightres'; // hightres, hd1080, hd720, default - youtube video quality
var _bg_video_youtube_start =                     86; // seconds - video start time
var _bg_video_youtube_end =                       274; // seconds - video end time, 0 to ignored
var _bg_video_youtube_loop =                      true; // true, false - video loop

//
// animation
// --------------------------------------------------
//

// 0 - disable, 1 - constellation, 2 - parallax star, 3 - particles
var _site_bg_animation =                          3;

// [true / false] - enable / disable parallax effect on `constellation` and `parallax star`
var _side_bg_effect_parallax =                    false;

//
// if `_site_bg_animation` == 1 (constellation)
// --------------------------------------------------
//

// [rgba format] - constellation color
var _constellation_color =                        'rgba(255, 255, 255, .9)';

// px - constellation width
var _constellation_width =                        1.5;

//
// if `_site_bg_animation` == 2 (parallax star)
// --------------------------------------------------
//

// 0.1 to 1 - parallax star opacity
var _parallax_star_opacity =                      1;

//
// if `_site_bg_animation` == 3 (particles)
// --------------------------------------------------
//

// 0.1 to 1 - particles opacity
var _particles_opacity =                          0.5;

// 0.1 to 1 - particles link opacity
var _particles_link_opacity =                     0.4;

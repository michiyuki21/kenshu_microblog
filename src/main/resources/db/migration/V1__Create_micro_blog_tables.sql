DROP TABLE IF EXISTS user;
create table user (
    id int(11) Not null Primary key Auto_increment Comment 'ユーザID',
    email varchar(255) Not null Unique Comment 'メールアドレス',
    password varchar(255) Not null Comment 'パスワード',
    nickname varchar(255) Not null Comment 'ニックネーム',
    modified_at datetime Not null Default current_timestamp() On update current_timestamp() Comment '最終更新時刻',
    created_at datetime Not null Default current_timestamp() Comment '登録日'
) Engine=InnoDB Default Charset=utf8mb4 Comment='ユーザ';

DROP TABLE IF EXISTS tweet;
create table tweet (
    id int(11) Not null Primary key Auto_increment Comment 'ID',
    user_id int(11) Not null Comment 'ユーザID',
    body varchar(255) Not null Comment 'つぶやき',
    modified_at datetime Not null Default current_timestamp() On update current_timestamp() Comment '最終更新時刻',
    created_at datetime Not null Default current_timestamp() Comment '登録日'
) Engine=InnoDB Default Charset=utf8mb4 Comment='つぶやき';

DROP TABLE IF EXISTS follow;
create table follow (
    id int(11) Not null Primary key Auto_increment Comment 'フォローID',
    user_id int(11) Not null Comment 'ユーザID',
    following_user_id int(11) Not null Comment 'フォローユーザID',
    Unique (user_id, following_user_id)
) Engine=InnoDB Default Charset=utf8mb4 Comment='フォロー';

DROP TABLE IF EXISTS favorite;
create table favorite (
    id int(11) Not null Primary key Auto_increment Comment 'お気に入りID',
    user_id int(11) Not null Comment 'ユーザID',
    tweet_id int(11) Not null Comment 'ツイートID'
) Engine=InnoDB Default Charset=utf8mb4 Comment='お気に入り';
# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

-- init script create procs
-- Inital script to create stored procedures etc for mysql platform
DROP PROCEDURE IF EXISTS usp_ebean_drop_foreign_keys;

delimiter $$
--
-- PROCEDURE: usp_ebean_drop_foreign_keys TABLE, COLUMN
-- deletes all constraints and foreign keys referring to TABLE.COLUMN
--
CREATE PROCEDURE usp_ebean_drop_foreign_keys(IN p_table_name VARCHAR(255), IN p_column_name VARCHAR(255))
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE c_fk_name CHAR(255);
  DECLARE curs CURSOR FOR SELECT CONSTRAINT_NAME from information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE() and TABLE_NAME = p_table_name and COLUMN_NAME = p_column_name
      AND REFERENCED_TABLE_NAME IS NOT NULL;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN curs;

  read_loop: LOOP
    FETCH curs INTO c_fk_name;
    IF done THEN
      LEAVE read_loop;
    END IF;
    SET @sql = CONCAT('ALTER TABLE ', p_table_name, ' DROP FOREIGN KEY ', c_fk_name);
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
  END LOOP;

  CLOSE curs;
END
$$

DROP PROCEDURE IF EXISTS usp_ebean_drop_column;

delimiter $$
--
-- PROCEDURE: usp_ebean_drop_column TABLE, COLUMN
-- deletes the column and ensures that all indices and constraints are dropped first
--
CREATE PROCEDURE usp_ebean_drop_column(IN p_table_name VARCHAR(255), IN p_column_name VARCHAR(255))
BEGIN
  CALL usp_ebean_drop_foreign_keys(p_table_name, p_column_name);
  SET @sql = CONCAT('ALTER TABLE ', p_table_name, ' DROP COLUMN ', p_column_name);
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
END
$$
create table cp_system_action (
  id                            varchar(255) auto_increment not null,
  action_name                   varchar(255),
  action_desc                   varchar(255),
  module_name                   varchar(255),
  module_desc                   varchar(255),
  need_show                     tinyint(1) default 0 not null,
  display_order                 integer not null,
  create_time                   bigint not null,
  constraint pk_cp_system_action primary key (id)
);

create table v1_activity (
  id                            bigint auto_increment not null,
  title                         varchar(255),
  head_pic                      varchar(255),
  address                       varchar(255),
  time_note                     varchar(255),
  end_time_note                 varchar(255),
  phonenumber1                  varchar(255),
  phonenumber2                  varchar(255),
  attend_numbers                bigint not null,
  details                       varchar(255),
  enroll_start_time             bigint not null,
  enroll_end_time               bigint not null,
  comments                      bigint not null,
  likes                         bigint not null,
  status                        integer not null,
  region_code                   varchar(255),
  region_name                   varchar(255),
  ticket_id                     bigint not null,
  sold_tickets                  bigint not null,
  total_tickets                 bigint not null,
  sponsor_id                    bigint not null,
  sponsor_logo                  varchar(255),
  sponsor_name                  varchar(255),
  sponsor_type                  integer not null,
  create_time                   bigint not null,
  constraint pk_v1_activity primary key (id)
);

create table v1_activity_attend (
  id                            bigint auto_increment not null,
  activity_id                   bigint not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_activity_attend primary key (id)
);

create table v1_activity_carousel (
  id                            bigint auto_increment not null,
  activity_id                   bigint not null,
  img_url                       varchar(255),
  link_url                      varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_activity_carousel primary key (id)
);

create table v1_activity_comment (
  id                            bigint auto_increment not null,
  content                       varchar(255),
  enable                        tinyint(1) default 0 not null,
  activity_id                   bigint not null,
  likes                         bigint not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_activity_comment primary key (id)
);

create table v1_activity_comment_like (
  id                            bigint auto_increment not null,
  comment_id                    bigint not null,
  has_like                      tinyint(1) default 0 not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_activity_comment_like primary key (id)
);

create table v1_activity_comment_reply (
  id                            bigint auto_increment not null,
  content                       varchar(255),
  comment_id                    bigint not null,
  at_uid                        bigint not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_activity_comment_reply primary key (id)
);

create table v1_activity_img (
  id                            bigint auto_increment not null,
  activity_id                   bigint not null,
  comment_id                    bigint not null,
  uid                           bigint not null,
  is_official                   tinyint(1) default 0 not null,
  img_url                       varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_activity_img primary key (id)
);

create table cp_member (
  id                            bigint auto_increment not null,
  username                      varchar(255),
  realname                      varchar(255),
  password                      varchar(255),
  create_time                   bigint not null,
  last_time                     bigint not null,
  last_ip                       varchar(255),
  org_id                        bigint not null,
  status                        integer not null,
  constraint pk_cp_member primary key (id)
);

create table v1_area_dealer (
  id                            bigint auto_increment not null,
  parent_id                     bigint not null,
  merchant_id                   bigint not null,
  is_parent                     tinyint(1) default 0 not null,
  title                         varchar(255),
  path                          varchar(255),
  ratio                         double not null,
  uid                           bigint not null,
  region_code                   varchar(255),
  user_name                     varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_area_dealer primary key (id)
);

create table v1_article (
  id                            bigint auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  source                        varchar(255),
  cate_id                       integer not null,
  publish_time                  bigint not null,
  status                        integer not null,
  is_top                        tinyint(1) default 0 not null,
  is_recommend                  tinyint(1) default 0 not null,
  sort                          integer not null,
  content                       varchar(255),
  digest                        varchar(255),
  head_pic                      varchar(255),
  views                         bigint not null,
  favs                          bigint not null,
  comments                      bigint not null,
  shares                        bigint not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_article primary key (id)
);

create table v1_article_category (
  id                            integer auto_increment not null,
  name                          varchar(255),
  sort                          integer not null,
  status                        integer not null,
  cate_type                     integer not null,
  note                          varchar(255),
  head_pic                      varchar(255),
  icon                          varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_article_category primary key (id)
);

create table v1_article_comment (
  id                            bigint auto_increment not null,
  content                       varchar(255),
  article_id                    bigint not null,
  uid                           bigint not null,
  likes                         bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_article_comment primary key (id)
);

create table v1_article_comment_like (
  id                            bigint auto_increment not null,
  comment_id                    bigint not null,
  has_like                      tinyint(1) default 0 not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_article_comment_like primary key (id)
);

create table v1_article_comment_reply (
  id                            bigint auto_increment not null,
  content                       varchar(255),
  article_comment_id            bigint not null,
  at_uid                        bigint not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_article_comment_reply primary key (id)
);

create table v1_article_fav (
  id                            bigint auto_increment not null,
  article_id                    bigint not null,
  uid                           bigint not null,
  enable                        tinyint(1) default 0 not null,
  create_time                   bigint not null,
  constraint pk_v1_article_fav primary key (id)
);

create table v1_balance_log (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  item_id                       integer not null,
  left_balance                  double not null,
  freeze_balance                double not null,
  total_balance                 double not null,
  change_amount                 double not null,
  biz_type                      integer not null,
  note                          varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_balance_log primary key (id)
);

create table v1_bar (
  id                            bigint auto_increment not null,
  bar_owner_id                  bigint not null,
  title                         varchar(255),
  address                       varchar(255),
  phonenumber1                  varchar(255),
  phonenumber2                  varchar(255),
  time_note                     varchar(255),
  digest                        varchar(255),
  head_pic                      varchar(255),
  logo                          varchar(255),
  comments                      bigint not null,
  signin_count                  bigint not null,
  likes                         bigint not null,
  favs                          bigint not null,
  stars                         integer not null,
  area                          varchar(255),
  desks                         integer not null,
  allow_smoke                   tinyint(1) default 0 not null,
  beer_cate                     integer not null,
  draft_beer                    integer not null,
  dinner                        varchar(255),
  style                         varchar(255),
  region_code                   varchar(255),
  region_name                   varchar(255),
  discount_info                 varchar(255),
  status                        integer not null,
  create_time                   bigint not null,
  constraint pk_v1_bar primary key (id)
);

create table v1_bar_carousel (
  id                            bigint auto_increment not null,
  bar_id                        bigint not null,
  img_url                       varchar(255),
  link_url                      varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_bar_carousel primary key (id)
);

create table v1_bar_comment (
  id                            bigint auto_increment not null,
  content                       varchar(255),
  enable                        tinyint(1) default 0 not null,
  bar_id                        bigint not null,
  beer_rate                     integer not null,
  env_rate                      integer not null,
  service_rate                  integer not null,
  average_consume               bigint not null,
  likes                         bigint not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_bar_comment primary key (id)
);

create table v1_bar_comment_like (
  id                            bigint auto_increment not null,
  comment_id                    bigint not null,
  has_like                      tinyint(1) default 0 not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_bar_comment_like primary key (id)
);

create table v1_bar_comment_reply (
  id                            bigint auto_increment not null,
  content                       varchar(255),
  bar_id                        bigint not null,
  bar_comment_id                bigint not null,
  at_uid                        bigint not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_bar_comment_reply primary key (id)
);

create table v1_bar_fav (
  id                            bigint auto_increment not null,
  bar_id                        bigint not null,
  uid                           bigint not null,
  enable                        tinyint(1) default 0 not null,
  create_time                   bigint not null,
  constraint pk_v1_bar_fav primary key (id)
);

create table v1_bar_follow (
  id                            bigint auto_increment not null,
  bar_id                        bigint not null,
  uid                           bigint not null,
  enable                        tinyint(1) default 0 not null,
  create_time                   bigint not null,
  constraint pk_v1_bar_follow primary key (id)
);

create table v1_bar_img (
  id                            bigint auto_increment not null,
  bar_id                        bigint not null,
  bar_comment_id                bigint not null,
  uid                           bigint not null,
  is_official                   tinyint(1) default 0 not null,
  img_url                       varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_bar_img primary key (id)
);

create table v1_bar_sign_in (
  id                            bigint auto_increment not null,
  bar_id                        bigint not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_bar_sign_in primary key (id)
);

create table v1_brand (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  url                           varchar(255),
  logo                          varchar(255),
  poster                        varchar(255),
  content                       varchar(255),
  sort                          integer not null,
  status                        integer not null,
  seo_title                     varchar(255),
  seo_keywords                  varchar(255),
  seo_description               varchar(255),
  show_at_home                  tinyint(1) default 0 not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_brand primary key (id)
);

create table v1_category (
  id                            bigint auto_increment not null,
  parent_id                     bigint not null,
  name                          varchar(255),
  img_url                       varchar(255),
  poster                        varchar(255),
  path                          varchar(255),
  is_shown                      integer not null,
  sort                          integer not null,
  sold_amount                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_category primary key (id)
);

create table v1_category_attr (
  id                            bigint auto_increment not null,
  category_id                   bigint not null,
  sys_cate_type_id              bigint not null,
  constraint pk_v1_category_attr primary key (id)
);

create table v1_charge (
  id                            bigint auto_increment not null,
  transaction_id                varchar(255),
  sub_id                        varchar(255),
  uid                           bigint not null,
  amount                        integer not null,
  status                        integer not null,
  pay_type                      integer not null,
  update_time                   bigint not null,
  constraint pk_v1_charge primary key (id)
);

create table v1_comment (
  id                            bigint auto_increment not null,
  product_id                    bigint not null,
  order_id                      bigint not null,
  content                       varchar(255),
  level                         integer not null,
  desc_star                     integer not null,
  logistics_star                integer not null,
  service_star                  integer not null,
  uid                           bigint not null,
  name                          varchar(255),
  type                          integer not null,
  reply_id                      bigint not null,
  has_append                    tinyint(1) default 0 not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_comment primary key (id)
);

create table v1_contact_detail (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  name                          varchar(255),
  province                      varchar(255),
  province_code                 varchar(255),
  city                          varchar(255),
  city_code                     varchar(255),
  area                          varchar(255),
  area_code                     varchar(255),
  details                       varchar(255),
  postcode                      varchar(255),
  telephone                     varchar(255),
  is_default                    integer not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_contact_detail primary key (id)
);

create table v1_coupon_config (
  id                            bigint auto_increment not null,
  coupon_title                  varchar(255),
  coupon_content                varchar(255),
  amount                        integer not null,
  type                          integer not null,
  status                        integer not null,
  claim_per_member              integer not null,
  total_amount                  integer not null,
  claim_amount                  integer not null,
  id_type                       integer not null,
  rule_content                  varchar(255),
  merchant_ids                  varchar(255),
  brand_ids                     varchar(255),
  bar_ids                       varchar(255),
  img_url                       varchar(255),
  begin_time                    bigint not null,
  end_time                      bigint not null,
  expire_days                   bigint not null,
  old_price                     integer not null,
  current_price                 integer not null,
  update_time                   bigint not null,
  constraint pk_v1_coupon_config primary key (id)
);

create table v1_dealer (
  id                            bigint auto_increment not null,
  type                          integer not null,
  dealer_name                   varchar(255),
  dealer_contact_detail         varchar(255),
  join_time                     bigint not null,
  description                   varchar(255),
  likes                         bigint not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_dealer primary key (id)
);

create table v1_dealer_profession (
  id                            bigint auto_increment not null,
  profession                    varchar(255),
  constraint pk_v1_dealer_profession primary key (id)
);

create table v1_dealer_submit_log (
  id                            bigint auto_increment not null,
  phone_number                  varchar(255),
  name                          varchar(255),
  profession_id                 bigint not null,
  profession                    varchar(255),
  status                        integer not null,
  note                          varchar(255),
  handler                       varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_dealer_submit_log primary key (id)
);

create table v1_dict (
  id                            bigint auto_increment not null,
  type                          integer not null,
  value                         varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_dict primary key (id)
);

create table v1_flash_sale (
  id                            bigint auto_increment not null,
  display_time                  varchar(255),
  begintime                     bigint not null,
  endtime                       bigint not null,
  status                        integer not null,
  constraint pk_v1_flash_sale primary key (id)
);

create table v1_flash_sale_product (
  id                            bigint auto_increment not null,
  product_id                    bigint not null,
  flash_sale_id                 bigint not null,
  product_head_pic              varchar(255),
  title                         varchar(255),
  price                         bigint not null,
  total_count                   bigint not null,
  sold_count                    bigint not null,
  begin_time                    bigint not null,
  end_time                      bigint not null,
  duration                      bigint not null,
  status                        integer not null,
  sort                          bigint not null,
  constraint pk_v1_flash_sale_product primary key (id)
);

create table cp_group (
  id                            integer auto_increment not null,
  name                          varchar(255),
  description                   varchar(255),
  create_time                   bigint not null,
  constraint pk_cp_group primary key (id)
);

create table cp_group_action (
  id                            integer auto_increment not null,
  group_id                      integer not null,
  system_action_id              varchar(255),
  constraint pk_cp_group_action primary key (id)
);

create table cp_group_user (
  id                            bigint auto_increment not null,
  group_id                      integer not null,
  group_name                    varchar(255),
  member_id                     bigint not null,
  realname                      varchar(255),
  create_time                   bigint not null,
  constraint pk_cp_group_user primary key (id)
);

create table cp_log (
  log_id                        bigint auto_increment not null,
  log_unique                    varchar(255),
  log_sym_id                    varchar(255),
  log_mer_id                    integer not null,
  log_param                     varchar(255),
  log_created                   bigint not null,
  constraint pk_cp_log primary key (log_id)
);

create table v1_logistics (
  id                            bigint auto_increment not null,
  order_id                      bigint not null,
  express_no                    varchar(255),
  consignee_realname            varchar(255),
  consignee_phone_number        varchar(255),
  consignee_phone_number2       varchar(255),
  consignee_address             varchar(255),
  consignee_postcode            varchar(255),
  logistics_type                integer not null,
  logistics_fee                 bigint not null,
  logistics_status              integer not null,
  logistics_settlement_status   integer not null,
  logistics_last_desc           varchar(255),
  logistics_desc                varchar(255),
  settlement_time               bigint not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_logistics primary key (id)
);

create table v1_mail_fee_config (
  id                            integer auto_increment not null,
  region_code                   varchar(255),
  region_name                   varchar(255),
  fee                           integer not null,
  up_to                         integer not null,
  update_time                   bigint not null,
  constraint pk_v1_mail_fee_config primary key (id)
);

create table v1_member (
  id                            bigint auto_increment not null,
  login_password                varchar(255),
  pay_password                  varchar(255),
  status                        integer not null,
  real_name                     varchar(255),
  nick_name                     varchar(255),
  phone_number                  varchar(255),
  description                   varchar(255),
  birthday                      bigint not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  allow_publish                 tinyint(1) default 0 not null,
  level                         integer not null,
  agent_code                    varchar(255),
  id_card_no                    varchar(255),
  open_id                       varchar(255),
  union_id                      varchar(255),
  session_key                   varchar(255),
  gender                        integer not null,
  city                          varchar(255),
  province                      varchar(255),
  country                       varchar(255),
  country_code                  varchar(255),
  agent_id                      bigint not null,
  avatar                        varchar(255),
  sign_phase                    varchar(255),
  follow_count                  bigint not null,
  fans_count                    bigint not null,
  favs_count                    bigint not null,
  bg_img_url                    varchar(255),
  continuation_sign_days        bigint not null,
  constraint pk_v1_member primary key (id)
);

create table v1_member_balance (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  item_id                       integer not null,
  left_balance                  double not null,
  freeze_balance                double not null,
  total_balance                 double not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_member_balance primary key (id)
);

create table v1_member_coupon (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  coupon_id                     bigint not null,
  begin_time                    bigint not null,
  end_time                      bigint not null,
  status                        bigint not null,
  code                          varchar(255),
  tx_id                         varchar(255),
  sub_id                        varchar(255),
  pay_type                      integer not null,
  real_pay                      integer not null,
  update_time                   bigint not null,
  constraint pk_v1_member_coupon primary key (id)
);

create table v1_member_level (
  id                            integer auto_increment not null,
  need_score                    bigint not null,
  level                         integer not null,
  level_name                    varchar(255),
  order_discount                integer not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_member_level primary key (id)
);

create table v1_member_like (
  id                            bigint auto_increment not null,
  member_id                     bigint not null,
  type                          integer not null,
  target_id                     bigint not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_member_like primary key (id)
);

create table v1_member_score_config (
  id                            bigint auto_increment not null,
  type                          integer not null,
  description                   varchar(255),
  score                         bigint not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_member_score_config primary key (id)
);

create table v1_member_score_log (
  id                            bigint auto_increment not null,
  member_id                     bigint not null,
  score                         bigint not null,
  type                          integer not null,
  reason_type                   integer not null,
  description                   varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_member_score_log primary key (id)
);

create table v1_mix_option (
  id                            integer auto_increment not null,
  amount                        integer not null,
  mix_code                      varchar(255),
  constraint pk_v1_mix_option primary key (id)
);

create table v1_operation_log (
  id                            bigint auto_increment not null,
  admin_id                      bigint not null,
  admin_name                    varchar(255),
  ip                            varchar(255),
  place                         varchar(255),
  note                          varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_operation_log primary key (id)
);

create table v1_order (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  user_name                     varchar(255),
  order_no                      varchar(255),
  tx_id                         varchar(255),
  org_id                        bigint not null,
  org_name                      varchar(255),
  score_gave                    bigint not null,
  status                        integer not null,
  post_service_status           integer not null,
  product_count                 integer not null,
  total_money                   double not null,
  real_pay                      double not null,
  logistics_fee                 double not null,
  address                       varchar(255),
  pay_method                    integer not null,
  out_trade_no                  varchar(255),
  pay_tx_no                     varchar(255),
  pay_time                      bigint not null,
  delivery_time                 bigint not null,
  score_use                     integer not null,
  score_to_money                double not null,
  order_settlement_time         bigint not null,
  is_mix                        tinyint(1) default 0 not null,
  coupon_id                     bigint not null,
  coupon_free                   double not null,
  commission_handled            tinyint(1) default 0 not null,
  region_path                   varchar(255),
  refund_tx_id                  varchar(255),
  express_no                    varchar(255),
  express_company               varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_order primary key (id)
);

create table v1_order_detail (
  id                            bigint auto_increment not null,
  order_id                      bigint not null,
  product_id                    bigint not null,
  product_name                  varchar(255),
  product_price                 double not null,
  sku_id                        bigint not null,
  sku_name                      varchar(255),
  unit                          varchar(255),
  product_img_url               varchar(255),
  product_mode_desc             varchar(255),
  product_mode_params           varchar(255),
  discount_rate                 integer not null,
  discount_amount               double not null,
  number                        bigint not null,
  sub_total                     double not null,
  is_product_available          tinyint(1) default 0 not null,
  remark                        varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_order_detail primary key (id)
);

create table v1_order_returns (
  id                            bigint auto_increment not null,
  returns_no                    varchar(255),
  order_no                      varchar(255),
  order_detail_id               bigint not null,
  express_no                    varchar(255),
  consignee_realname            varchar(255),
  consignee_phone_number        varchar(255),
  consignee_address             varchar(255),
  consignee_postcode            varchar(255),
  logis_name                    varchar(255),
  state                         integer not null,
  pre_status                    integer not null,
  status                        integer not null,
  uid                           bigint not null,
  operator_id                   bigint not null,
  operator_name                 varchar(255),
  reason                        varchar(255),
  logistics_last_desc           varchar(255),
  logistics_desc                varchar(255),
  return_type                   integer not null,
  handling_way                  varchar(255),
  return_money                  bigint not null,
  return_submit_time            bigint not null,
  handling_return_time          bigint not null,
  remark                        varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_order_returns primary key (id)
);

create table v1_order_returns_apply (
  id                            bigint auto_increment not null,
  order_no                      varchar(255),
  order_detail_id               bigint not null,
  returns_no                    varchar(255),
  uid                           bigint not null,
  state                         integer not null,
  delivery_status               integer not null,
  reason                        varchar(255),
  status                        integer not null,
  return_submit_time            bigint not null,
  audit_time                    bigint not null,
  audit_content                 varchar(255),
  note                          varchar(255),
  operator_id                   bigint not null,
  operator_name                 varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_order_returns_apply primary key (id)
);

create table v1_org (
  id                            integer auto_increment not null,
  status                        integer not null,
  name                          varchar(255),
  contact_number                varchar(255),
  contact_name                  varchar(255),
  contact_address               varchar(255),
  license_number                varchar(255),
  license_img                   varchar(255),
  license_thumb_img             varchar(255),
  description                   varchar(255),
  approve_note                  varchar(255),
  log                           varchar(255),
  creator_id                    bigint not null,
  approver_id                   bigint not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_org primary key (id)
);

create table v1_system_config (
  id                            integer auto_increment not null,
  config_key                    varchar(255),
  config_value                  varchar(255),
  note                          varchar(255),
  update_time                   bigint not null,
  constraint pk_v1_system_config primary key (id)
);

create table v1_product (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  category_id                   bigint not null,
  brand_id                      bigint not null,
  wine_id                       bigint not null,
  org_id                        bigint not null,
  mail_fee_id                   bigint not null,
  type_id                       bigint not null,
  sketch                        varchar(255),
  details                       varchar(255),
  keywords                      varchar(255),
  tag                           varchar(255),
  marque                        varchar(255),
  barcode                       varchar(255),
  virtual_count                 bigint not null,
  price                         double not null,
  wholesale_price               double not null,
  cost_price                    double not null,
  market_price                  double not null,
  max_score_used                integer not null,
  stock                         bigint not null,
  sold_amount                   bigint not null,
  warning_stock                 bigint not null,
  cover_img_url                 varchar(255),
  poster                        varchar(255),
  mix_code                      varchar(255),
  status                        integer not null,
  state                         integer not null,
  is_combo                      tinyint(1) default 0 not null,
  allow_use_score               tinyint(1) default 0 not null,
  sort                          integer not null,
  unit                          varchar(255),
  deleted_at                    bigint not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_product primary key (id)
);

create table v1_product_attr (
  id                            bigint auto_increment not null,
  product_id                    bigint not null,
  name                          varchar(255),
  sort                          integer not null,
  constraint pk_v1_product_attr primary key (id)
);

create table v1_product_attr_option (
  id                            bigint auto_increment not null,
  option_id                     bigint not null,
  name                          varchar(255),
  attr_id                       bigint not null,
  supplier_option_id            bigint not null,
  sort                          integer not null,
  constraint pk_v1_product_attr_option primary key (id)
);

create table v1_product_classify (
  id                            bigint auto_increment not null,
  classify_code                 varchar(255),
  sort                          bigint not null,
  product_count                 integer not null,
  constraint pk_v1_product_classify primary key (id)
);

create table v1_product_classify_detail (
  id                            bigint auto_increment not null,
  product_id                    bigint not null,
  classify_id                   bigint not null,
  sort                          bigint not null,
  constraint pk_v1_product_classify_detail primary key (id)
);

create table v1_product_img (
  id                            bigint auto_increment not null,
  img_url                       varchar(255),
  product_id                    bigint not null,
  tips                          varchar(255),
  sort                          integer not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_product_img primary key (id)
);

create table v1_product_mix (
  id                            bigint auto_increment not null,
  product_id                    bigint not null,
  mix_code                      varchar(255),
  constraint pk_v1_product_mix primary key (id)
);

create table v1_product_param (
  id                            bigint auto_increment not null,
  product_id                    bigint not null,
  name                          varchar(255),
  value                         varchar(255),
  constraint pk_v1_product_param primary key (id)
);

create table v1_product_sku (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  product_id                    bigint not null,
  img_url                       varchar(255),
  price                         double not null,
  sold_amount                   bigint not null,
  stock                         bigint not null,
  sort                          integer not null,
  code                          varchar(255),
  barcode                       varchar(255),
  data                          varchar(255),
  constraint pk_v1_product_sku primary key (id)
);

create table v1_product_tab (
  id                            bigint auto_increment not null,
  tab_name                      varchar(255),
  sort                          integer not null,
  constraint pk_v1_product_tab primary key (id)
);

create table v1_product_tab_classify (
  id                            bigint auto_increment not null,
  product_tab_id                bigint not null,
  classify_id                   bigint not null,
  classify_cover_img_url        varchar(255),
  classify_name                 varchar(255),
  sort                          integer not null,
  constraint pk_v1_product_tab_classify primary key (id)
);

create table v1_product_tab_products (
  id                            bigint auto_increment not null,
  product_tab_id                bigint not null,
  product_id                    bigint not null,
  sort                          integer not null,
  constraint pk_v1_product_tab_products primary key (id)
);

create table v1_product_tag (
  id                            bigint auto_increment not null,
  product_id                    bigint not null,
  tag                           varchar(255),
  constraint pk_v1_product_tag primary key (id)
);

create table v1_promot (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  invite_count                  integer not null,
  income                        bigint not null,
  status                        integer not null,
  invite_code                   varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_promot primary key (id)
);

create table v1_promot_member (
  id                            bigint auto_increment not null,
  promotion_id                  bigint not null,
  uid                           bigint not null,
  master_id                     bigint not null,
  is_award                      integer not null,
  create_time                   bigint not null,
  constraint pk_v1_promot_member primary key (id)
);

create table v1_region (
  id                            integer auto_increment not null,
  region_code                   varchar(255),
  region_name                   varchar(255),
  parent_id                     integer not null,
  region_level                  integer not null,
  region_order                  integer not null,
  region_name_en                varchar(255),
  region_short_name_en          varchar(255),
  constraint pk_v1_region primary key (id)
);

create table v1_return_contact_detail (
  id                            bigint auto_increment not null,
  org_id                        bigint not null,
  name                          varchar(255),
  province                      varchar(255),
  province_code                 varchar(255),
  city                          varchar(255),
  city_code                     varchar(255),
  area                          varchar(255),
  area_code                     varchar(255),
  details                       varchar(255),
  postcode                      varchar(255),
  telephone                     varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_return_contact_detail primary key (id)
);

create table v1_search_key_word (
  id                            integer auto_increment not null,
  key_word                      varchar(255),
  sort                          integer not null,
  constraint pk_v1_search_key_word primary key (id)
);

create table v1_shopping_cart (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  product_id                    bigint not null,
  sku_id                        bigint not null,
  amount                        bigint not null,
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_shopping_cart primary key (id)
);

create table v1_product_special_topic (
  id                            bigint auto_increment not null,
  cover_img_url                 varchar(255),
  title                         varchar(255),
  details                       varchar(255),
  product_count                 integer not null,
  status                        integer not null,
  create_time                   bigint not null,
  constraint pk_v1_product_special_topic primary key (id)
);

create table v1_product_st_list (
  id                            bigint auto_increment not null,
  product_id                    bigint not null,
  topic_id                      bigint not null,
  constraint pk_v1_product_st_list primary key (id)
);

create table v1_stat_day_region (
  id                            bigint auto_increment not null,
  region_code                   varchar(255),
  day                           varchar(255),
  commission                    double not null,
  order_amount                  double not null,
  create_time                   bigint not null,
  constraint pk_v1_stat_day_region primary key (id)
);

create table v1_stat_member_day (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  user_name                     varchar(255),
  day                           varchar(255),
  commission                    double not null,
  order_amount                  double not null,
  create_time                   bigint not null,
  constraint pk_v1_stat_member_day primary key (id)
);

create table v1_stat_member_month (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  user_name                     varchar(255),
  month                         varchar(255),
  commission                    double not null,
  order_amount                  double not null,
  create_time                   bigint not null,
  constraint pk_v1_stat_member_month primary key (id)
);

create table v1_stat_member_total (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  user_name                     varchar(255),
  commission                    double not null,
  order_amount                  double not null,
  create_time                   bigint not null,
  constraint pk_v1_stat_member_total primary key (id)
);

create table v1_stat_month_region (
  id                            bigint auto_increment not null,
  region_code                   varchar(255),
  month                         varchar(255),
  commission                    double not null,
  order_amount                  double not null,
  create_time                   bigint not null,
  constraint pk_v1_stat_month_region primary key (id)
);

create table v1_stat_platform_day (
  id                            bigint auto_increment not null,
  day                           varchar(255),
  total_money                   double not null,
  total_commission              double not null,
  platform_commission           double not null,
  create_time                   bigint not null,
  constraint pk_v1_stat_platform_day primary key (id)
);

create table v1_stat_platform_month (
  id                            bigint auto_increment not null,
  month                         varchar(255),
  total_money                   double not null,
  total_commission              double not null,
  platform_commission           double not null,
  create_time                   bigint not null,
  constraint pk_v1_stat_platform_month primary key (id)
);

create table v1_stat_overview_region (
  id                            bigint auto_increment not null,
  region_code                   varchar(255),
  commission                    double not null,
  order_amount                  double not null,
  create_time                   bigint not null,
  constraint pk_v1_stat_overview_region primary key (id)
);

create table v1_stat_overview_total (
  id                            bigint auto_increment not null,
  commission                    double not null,
  order_amount                  double not null,
  create_time                   bigint not null,
  constraint pk_v1_stat_overview_total primary key (id)
);

create table v1_stat_reg (
  id                            bigint auto_increment not null,
  total                         integer not null,
  date                          varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_stat_reg primary key (id)
);

create table v1_system_attr (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  sys_cate_type_id              bigint not null,
  sort                          integer not null,
  constraint pk_v1_system_attr primary key (id)
);

create table v1_system_attr_option (
  id                            bigint auto_increment not null,
  attr_id                       bigint not null,
  option_id                     bigint not null,
  name                          varchar(255),
  sort                          integer not null,
  constraint pk_v1_system_attr_option primary key (id)
);

create table v1_system_carousel (
  id                            integer auto_increment not null,
  name                          varchar(255),
  img_url                       varchar(255),
  link_url                      varchar(255),
  mobile_img_url                varchar(255),
  mobile_link_url               varchar(255),
  client_type                   integer not null,
  biz_type                      integer not null,
  sort                          integer not null,
  need_show                     tinyint(1) default 0 not null,
  title1                        varchar(255),
  title2                        varchar(255),
  note                          varchar(255),
  region_code                   varchar(255),
  region_name                   varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_system_carousel primary key (id)
);

create table v1_system_category_type (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  attr_count                    integer not null,
  param_count                   integer not null,
  sort                          integer not null,
  constraint pk_v1_system_category_type primary key (id)
);

create table v1_system_link (
  id                            integer auto_increment not null,
  name                          varchar(255),
  url                           varchar(255),
  sort                          integer not null,
  status                        integer not null,
  note                          varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_system_link primary key (id)
);

create table v1_system_param (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  sys_cate_type_id              bigint not null,
  method                        varchar(255),
  value                         varchar(255),
  sort                          integer not null,
  constraint pk_v1_system_param primary key (id)
);

create table v1_team (
  id                            bigint auto_increment not null,
  cover_img_url                 varchar(255),
  logo                          varchar(255),
  team_name                     varchar(255),
  digest                        varchar(255),
  status                        integer not null,
  level                         integer not null,
  province_code                 varchar(255),
  region_code                   varchar(255),
  region_name                   varchar(255),
  members_number                bigint not null,
  leader_uid                    bigint not null,
  leader_name                   varchar(255),
  team_code                     varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_team primary key (id)
);

create table v1_team_consume (
  id                            integer auto_increment not null,
  team_id                       bigint not null,
  team_name                     varchar(255),
  team_cover_img_url            varchar(255),
  month                         varchar(255),
  consume_money                 bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_team_consume primary key (id)
);

create table v1_team_member (
  id                            integer auto_increment not null,
  team_id                       bigint not null,
  uid                           bigint not null,
  name                          varchar(255),
  total_money                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_team_member primary key (id)
);

create table v1_team_member_join_log (
  id                            bigint auto_increment not null,
  team_id                       bigint not null,
  uid                           bigint not null,
  name                          varchar(255),
  status                        integer not null,
  create_time                   bigint not null,
  constraint pk_v1_team_member_join_log primary key (id)
);

create table v1_team_member_order_log (
  id                            bigint auto_increment not null,
  team_id                       bigint not null,
  uid                           bigint not null,
  order_no                      varchar(255),
  order_status                  integer not null,
  order_money                   integer not null,
  create_time                   bigint not null,
  constraint pk_v1_team_member_order_log primary key (id)
);

create table v1_team_transfer_log (
  id                            bigint auto_increment not null,
  from_team_id                  bigint not null,
  from_team_name                varchar(255),
  to_team_id                    bigint not null,
  to_team_name                  varchar(255),
  members                       varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_team_transfer_log primary key (id)
);

create table v1_timeline (
  id                            bigint auto_increment not null,
  author_id                     bigint not null,
  status                        integer not null,
  timeline_type                 integer not null,
  content                       varchar(255),
  tag                           varchar(255),
  views                         bigint not null,
  likes                         bigint not null,
  shares                        bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_timeline primary key (id)
);

create table v1_timeline_comment (
  id                            bigint auto_increment not null,
  content                       varchar(255),
  timeline_id                   bigint not null,
  uid                           bigint not null,
  at_uid                        bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_timeline_comment primary key (id)
);

create table v1_timeline_follow (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  author_id                     bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_timeline_follow primary key (id)
);

create table v1_timeline_img (
  id                            bigint auto_increment not null,
  timeline_id                   bigint not null,
  uid                           bigint not null,
  img_url                       varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_timeline_img primary key (id)
);

create table v1_timeline_likes (
  id                            bigint auto_increment not null,
  liker_uid                     bigint not null,
  timeline_id                   bigint not null,
  has_like                      tinyint(1) default 0 not null,
  create_time                   bigint not null,
  constraint pk_v1_timeline_likes primary key (id)
);

create table v1_timeline_list (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  article_id                    bigint not null,
  author_id                     bigint not null,
  enable                        tinyint(1) default 0 not null,
  create_time                   bigint not null,
  constraint pk_v1_timeline_list primary key (id)
);

create table v1_timeline_tag (
  id                            bigint auto_increment not null,
  tag                           varchar(255),
  img_url                       varchar(255),
  uid                           bigint not null,
  constraint pk_v1_timeline_tag primary key (id)
);

create table v1_wine (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  name_en                       varchar(255),
  category_id                   varchar(255),
  details                       varchar(255),
  img_url                       varchar(255),
  style                         varchar(255),
  style_id                      bigint not null,
  product_id                    bigint not null,
  alcohol_percent               double not null,
  bitter_percent                double not null,
  brand_name                    varchar(255),
  production_place              varchar(255),
  status                        integer not null,
  wanna_count                   bigint not null,
  drunk_count                   bigint not null,
  comments                      bigint not null,
  smell_rate                    integer not null,
  taste_rate                    integer not null,
  shape_rate                    integer not null,
  feel_rate                     integer not null,
  sort                          integer not null,
  tag                           varchar(255),
  update_time                   bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_wine primary key (id)
);

create table v1_wine_category (
  id                            bigint auto_increment not null,
  parent_id                     bigint not null,
  name                          varchar(255),
  img_url                       varchar(255),
  path                          varchar(255),
  is_shown                      integer not null,
  sort                          integer not null,
  create_time                   bigint not null,
  constraint pk_v1_wine_category primary key (id)
);

create table v1_wine_comment (
  id                            bigint auto_increment not null,
  wine_id                       bigint not null,
  content                       varchar(255),
  enable                        tinyint(1) default 0 not null,
  smell_rate                    integer not null,
  taste_rate                    integer not null,
  shape_rate                    integer not null,
  feel_rate                     integer not null,
  likes                         bigint not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_wine_comment primary key (id)
);

create table v1_wine_comment_like (
  id                            bigint auto_increment not null,
  comment_id                    bigint not null,
  has_like                      tinyint(1) default 0 not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_wine_comment_like primary key (id)
);

create table v1_wine_comment_reply (
  id                            bigint auto_increment not null,
  content                       varchar(255),
  wine_id                       bigint not null,
  comment_id                    bigint not null,
  at_uid                        bigint not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_wine_comment_reply primary key (id)
);

create table v1_wine_cup_shape (
  id                            bigint auto_increment not null,
  img_url                       varchar(255),
  name                          varchar(255),
  name_en                       varchar(255),
  constraint pk_v1_wine_cup_shape primary key (id)
);

create table v1_wine_style_cup_shape_match (
  id                            bigint auto_increment not null,
  style_id                      bigint not null,
  cup_shape_id                  bigint not null,
  constraint pk_v1_wine_style_cup_shape_match primary key (id)
);

create table v1_wine_drunk_list (
  id                            bigint auto_increment not null,
  wine_id                       bigint not null,
  uid                           bigint not null,
  enable                        tinyint(1) default 0 not null,
  create_time                   bigint not null,
  constraint pk_v1_wine_drunk_list primary key (id)
);

create table v1_wine_img (
  id                            bigint auto_increment not null,
  wine_id                       bigint not null,
  wine_comment_id               bigint not null,
  uid                           bigint not null,
  img_url                       varchar(255),
  create_time                   bigint not null,
  constraint pk_v1_wine_img primary key (id)
);

create table v1_wine_style (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  name_en                       varchar(255),
  details                       varchar(255),
  abv_from                      double not null,
  abv_to                        double not null,
  ibu_from                      double not null,
  ibu_to                        double not null,
  srm_from                      double not null,
  srm_to                        double not null,
  co2_from                      double not null,
  co2_to                        double not null,
  ferment_from                  double not null,
  ferment_to                    double not null,
  create_time                   bigint not null,
  constraint pk_v1_wine_style primary key (id)
);

create table v1_wine_style_food (
  id                            bigint auto_increment not null,
  img_url                       varchar(255),
  name                          varchar(255),
  name_en                       varchar(255),
  constraint pk_v1_wine_style_food primary key (id)
);

create table v1_wine_style_food_match (
  id                            bigint auto_increment not null,
  style_id                      bigint not null,
  food_id                       bigint not null,
  constraint pk_v1_wine_style_food_match primary key (id)
);

create table v1_wine_wish_list (
  id                            bigint auto_increment not null,
  wine_id                       bigint not null,
  enable                        tinyint(1) default 0 not null,
  uid                           bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_wine_wish_list primary key (id)
);

create table v1_withdraw_log (
  id                            bigint auto_increment not null,
  uid                           bigint not null,
  amount                        bigint not null,
  real_name                     varchar(255),
  bank_number                   varchar(255),
  bank_name                     varchar(255),
  operator_id                   bigint not null,
  operator_name                 varchar(255),
  status                        integer not null,
  note                          varchar(255),
  audit_time                    bigint not null,
  create_time                   bigint not null,
  constraint pk_v1_withdraw_log primary key (id)
);


# --- !Downs

drop table if exists cp_system_action;

drop table if exists v1_activity;

drop table if exists v1_activity_attend;

drop table if exists v1_activity_carousel;

drop table if exists v1_activity_comment;

drop table if exists v1_activity_comment_like;

drop table if exists v1_activity_comment_reply;

drop table if exists v1_activity_img;

drop table if exists cp_member;

drop table if exists v1_area_dealer;

drop table if exists v1_article;

drop table if exists v1_article_category;

drop table if exists v1_article_comment;

drop table if exists v1_article_comment_like;

drop table if exists v1_article_comment_reply;

drop table if exists v1_article_fav;

drop table if exists v1_balance_log;

drop table if exists v1_bar;

drop table if exists v1_bar_carousel;

drop table if exists v1_bar_comment;

drop table if exists v1_bar_comment_like;

drop table if exists v1_bar_comment_reply;

drop table if exists v1_bar_fav;

drop table if exists v1_bar_follow;

drop table if exists v1_bar_img;

drop table if exists v1_bar_sign_in;

drop table if exists v1_brand;

drop table if exists v1_category;

drop table if exists v1_category_attr;

drop table if exists v1_charge;

drop table if exists v1_comment;

drop table if exists v1_contact_detail;

drop table if exists v1_coupon_config;

drop table if exists v1_dealer;

drop table if exists v1_dealer_profession;

drop table if exists v1_dealer_submit_log;

drop table if exists v1_dict;

drop table if exists v1_flash_sale;

drop table if exists v1_flash_sale_product;

drop table if exists cp_group;

drop table if exists cp_group_action;

drop table if exists cp_group_user;

drop table if exists cp_log;

drop table if exists v1_logistics;

drop table if exists v1_mail_fee_config;

drop table if exists v1_member;

drop table if exists v1_member_balance;

drop table if exists v1_member_coupon;

drop table if exists v1_member_level;

drop table if exists v1_member_like;

drop table if exists v1_member_score_config;

drop table if exists v1_member_score_log;

drop table if exists v1_mix_option;

drop table if exists v1_operation_log;

drop table if exists v1_order;

drop table if exists v1_order_detail;

drop table if exists v1_order_returns;

drop table if exists v1_order_returns_apply;

drop table if exists v1_org;

drop table if exists v1_system_config;

drop table if exists v1_product;

drop table if exists v1_product_attr;

drop table if exists v1_product_attr_option;

drop table if exists v1_product_classify;

drop table if exists v1_product_classify_detail;

drop table if exists v1_product_img;

drop table if exists v1_product_mix;

drop table if exists v1_product_param;

drop table if exists v1_product_sku;

drop table if exists v1_product_tab;

drop table if exists v1_product_tab_classify;

drop table if exists v1_product_tab_products;

drop table if exists v1_product_tag;

drop table if exists v1_promot;

drop table if exists v1_promot_member;

drop table if exists v1_region;

drop table if exists v1_return_contact_detail;

drop table if exists v1_search_key_word;

drop table if exists v1_shopping_cart;

drop table if exists v1_product_special_topic;

drop table if exists v1_product_st_list;

drop table if exists v1_stat_day_region;

drop table if exists v1_stat_member_day;

drop table if exists v1_stat_member_month;

drop table if exists v1_stat_member_total;

drop table if exists v1_stat_month_region;

drop table if exists v1_stat_platform_day;

drop table if exists v1_stat_platform_month;

drop table if exists v1_stat_overview_region;

drop table if exists v1_stat_overview_total;

drop table if exists v1_stat_reg;

drop table if exists v1_system_attr;

drop table if exists v1_system_attr_option;

drop table if exists v1_system_carousel;

drop table if exists v1_system_category_type;

drop table if exists v1_system_link;

drop table if exists v1_system_param;

drop table if exists v1_team;

drop table if exists v1_team_consume;

drop table if exists v1_team_member;

drop table if exists v1_team_member_join_log;

drop table if exists v1_team_member_order_log;

drop table if exists v1_team_transfer_log;

drop table if exists v1_timeline;

drop table if exists v1_timeline_comment;

drop table if exists v1_timeline_follow;

drop table if exists v1_timeline_img;

drop table if exists v1_timeline_likes;

drop table if exists v1_timeline_list;

drop table if exists v1_timeline_tag;

drop table if exists v1_wine;

drop table if exists v1_wine_category;

drop table if exists v1_wine_comment;

drop table if exists v1_wine_comment_like;

drop table if exists v1_wine_comment_reply;

drop table if exists v1_wine_cup_shape;

drop table if exists v1_wine_style_cup_shape_match;

drop table if exists v1_wine_drunk_list;

drop table if exists v1_wine_img;

drop table if exists v1_wine_style;

drop table if exists v1_wine_style_food;

drop table if exists v1_wine_style_food_match;

drop table if exists v1_wine_wish_list;

drop table if exists v1_withdraw_log;


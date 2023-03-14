DROP TABLE IF EXISTS user_to_group, users, groups, exercises_to_training_plan, training_to_exercise, exercises, training_plans, training;

CREATE TABLE users
(
    username VARCHAR(30) NOT NULL,
    password VARCHAR(64) NOT NULL,
    isAdmin  BOOLEAN     NOT NULL,
    PRIMARY KEY (username)
);

INSERT INTO users(username, password, isAdmin)
VALUES ('Heiko23', '39244e2c92cc9d93647e26d25f57d1523d95f8244fda422176de24a9d128ce03', true);
INSERT INTO users(username, password, isAdmin)
VALUES ('Felix90', '031372975e538645a768879cca22575e891456c2abcc9428af669daed2a7460a', true);
INSERT INTO users(username, password, isAdmin)
VALUES ('Boris81', 'edd9a8a8b629ce59d162ffef7a6a1daa8ba4ca66a416ec3d4011d343dbcfa3ba', true);
INSERT INTO users(username, password, isAdmin)
VALUES ('Jannik3', '9af3c14b5dc44330daf087e8aa46f71b83490ad21c537010d40fa7933bcd3108', false);
INSERT INTO users(username, password, isAdmin)
VALUES ('Anika25', '4de5be6a0531c585a14b039be3ee682e6841988fd15c75c007cbd085daa1bde6', false);
INSERT INTO users(username, password, isAdmin)
VALUES ('Lara919', 'cdf782037f89817784ad3923bcc46867bcb995933032838ef1ed64fcb9b91bea', false);
INSERT INTO users(username, password, isAdmin)
VALUES ('Petra87', '71e0d2f59ae28d3444b3406ad4d7fc0aa07a7566e715849c7ed62793ff3ad176', false);

CREATE TABLE groups
(
    groupname VARCHAR(50) NOT NULL,
    PRIMARY KEY (groupname)
);

CREATE TABLE user_to_group
(
    username  VARCHAR(30) NOT NULL,
    groupname VARCHAR(50) NOT NULL,
    PRIMARY KEY (username, groupname),
    CONSTRAINT FK__group FOREIGN KEY (groupname) REFERENCES groups (groupname)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FK__users FOREIGN KEY (username) REFERENCES users (username)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE exercises
(
    exercisename VARCHAR(50) NOT NULL,
    PRIMARY KEY (exercisename)
);

INSERT INTO exercises(exercisename)
VALUES ('Liegestütze');
INSERT INTO exercises(exercisename)
VALUES ('Situps');
INSERT INTO exercises(exercisename)
VALUES ('Superman');
INSERT INTO exercises(exercisename)
VALUES ('Planks');
INSERT INTO exercises(exercisename)
VALUES ('Pushups');
INSERT INTO exercises(exercisename)
VALUES ('Bergsteiger');

CREATE TABLE training_plans
(
    planname   VARCHAR(50) NOT NULL,
    validFrom  DATE        NOT NULL,
    validUntil DATE        NOT NULL,
    PRIMARY KEY (planname)
);

CREATE TABLE exercises_to_training_plan
(
    exercisename VARCHAR(50) NOT NULL,
    planname     VARCHAR(50) NOT NULL,
    PRIMARY KEY (exercisename, planname),
    CONSTRAINT FK__exercises FOREIGN KEY (exercisename) REFERENCES exercises (exercisename)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FK_exercises_to_training_plan_training_plans FOREIGN KEY (planname) REFERENCES training_plans (planname)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE training
(
    username VARCHAR(30) NOT NULL,
    date     DATE        NOT NULL,
    planname VARCHAR(50) NOT NULL,
    PRIMARY KEY (username, date, planname),
    CONSTRAINT FK_training_user FOREIGN KEY (username) REFERENCES users (username)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FK_training_training_plans FOREIGN KEY (planname) REFERENCES training_plans (planname)
        ON UPDATE CASCADE
        ON DELETE Cascade
);

CREATE TABLE training_to_exercise
(
    username     VARCHAR(30) NOT NULL,
    date         DATE        NOT NULL,
    planname     VARCHAR(50) NOT NULL,
    exercisename VARCHAR(50) NOT NULL,
    is_finished  BOOLEAN     NOT NULL,
    PRIMARY KEY (username, date, planname, exercisename),
    CONSTRAINT FK_training_to_exercise_exercises FOREIGN KEY (exercisename) REFERENCES exercises (exercisename)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FK_training FOREIGN KEY (username, date, planname) REFERENCES training (username, date, planname)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

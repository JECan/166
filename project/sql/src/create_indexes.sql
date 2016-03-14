CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION next_val() RETURNS trigger as
	$BODY$
        BEGIN
		NEW.msg_id := nextval('message_msg_id_seq');
		return NEW;
	END;
        $BODY$
LANGUAGE plpgsql VOLATILE;


CREATE TRIGGER get_id BEFORE INSERT ON MESSAGE
for each row execute procedure next_val();

create index msg_id 
on message using btree
(sender_login, chat_id);

create index msg
on message using btree
(msg_id, chat_id);

create index chats
on chat using btree
(chat_id, init_sender);

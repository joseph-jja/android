/* Copyright (c) 2009 Christoph Studer <chstuder@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.backup.constants;

/**
 * Contains SMS content provider constants. These values are copied from
 * com.android.provider.telephony.*
 */
public final class SmsConstants {
    
    /* Known Fields in the Database ( excluding _id )
     * protocol
     * address
     * date 
     * type - used to identify sent or received message and possibly other types
     * subject 
     * body 
     * toa 
     * sc_toa 
     * service_center 
     * read 
     * status 
     * locked 
     * date_sent - 
     * readable_date - this is the date field reformatted in the format Mar 24, 2010 5:04:50 PM
     * contact_name - 
     * 
     * also 
     * thread_id
     * person
     * reply_path_present
     * index_on_sim
     * priority
     * 
     * vendors may have their own extensions
     */
    public static final String ID = "_id";

    public static final String ADDRESS = "address";

    public static final String PERSON = "person";

    public static final String CONTACT_NAME = "contact_name";

    public static final String BODY = "body";

    public static final String DATE = "date";

    public static final String DATE_SENT = "date_sent";

    public static final String READABLE_DATE = "readable_date";

    public static final String THREAD_ID = "thread_id";

    public static final String SUBJECT = "subject";

    public static final String TYPE = "type";

    public static final String READ = "read";

    public static final String STATUS = "status";

    public static final String LOCKED = "locked";

    public static final String SERVICE_CENTER = "service_center";

    public static final String PROTOCOL = "protocol";

    public static final String REPLAY_PATH = "reply_path_present";

    public static final String TOA = "toa";

    public static final String SC_TOA = "sc_toa";

    public static final int MESSAGE_TYPE_ALL = 0;

    public static final int MESSAGE_TYPE_INBOX = 1;

    public static final int MESSAGE_TYPE_SENT = 2;

    public static final int MESSAGE_TYPE_DRAFT = 3;

    public static final int MESSAGE_TYPE_OUTBOX = 4;

    public static final int MESSAGE_TYPE_FAILED = 5; // for failed outgoing
    // messages

    public static final int MESSAGE_TYPE_QUEUED = 6; // for messages to send
    // later
}
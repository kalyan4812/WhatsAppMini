package com.saikalyandaroju.whatsappclone.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.saikalyandaroju.whatsappclone.Models.ChatEvent;
import com.saikalyandaroju.whatsappclone.Models.DateHeader;
import com.saikalyandaroju.whatsappclone.Models.Message;
import com.saikalyandaroju.whatsappclone.R;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ChatActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int UNSUPPORTED = -1;
    private static final int TEXT_MESSAGE_RECEIVED = 0;
    private static final int TEXT_MESSAGE_SENT = 1;
    private static final int DATE_HEADER = 2;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    private List<ChatEvent> chatEvents;
    private String currentUserId;
    private HighFiveClickListener highFiveClickListener;
    private Context context;

    public ChatActivityAdapter(List<ChatEvent> chatEvents, String currentUserId) {
        this.chatEvents = chatEvents;
        this.currentUserId = currentUserId;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TEXT_MESSAGE_RECEIVED) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_recv_message, parent, false);
            return new MessageViewHolder(v);
        } else if (viewType == TEXT_MESSAGE_SENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_sent_message, parent, false);
            return new MessageViewHolder(v);

        } else if (viewType == DATE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_date_header, parent, false);
            return new DateViewHolder(v);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_recv_message, parent, false);
            return new MessageViewHolder(v);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ChatEvent chatEvent = chatEvents.get(position);
        if (chatEvent instanceof DateHeader) {
            DateViewHolder dateViewHolder = (DateViewHolder) holder;

            setDate(dateViewHolder, ((DateHeader) chatEvent).getSentAt());
        } else if (chatEvent instanceof Message) {
            MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
            messageViewHolder.content.setText(((Message) chatEvent).getMsg());
            messageViewHolder.time.setText(format(chatEvent.getSentAt()));
            messageViewHolder.highFive.setVisibility(((Message) chatEvent).getLiked() ? View.VISIBLE : View.GONE);
            switch (getItemViewType(position)) {
                case TEXT_MESSAGE_RECEIVED:
                    messageViewHolder.materialCardView.setOnClickListener(new DoubleClickListener() {
                        @Override
                        protected void onDoubleClick(View v) {
                            if (highFiveClickListener != null) {
                                highFiveClickListener.highFiveClick(((Message) chatEvent).getMsgId(), !((Message) chatEvent).getLiked());

                            }
                        }
                    });
                    messageViewHolder.highFive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (highFiveClickListener != null) {
                                highFiveClickListener.highFiveClick(((Message) chatEvent).getMsgId(), !((Message) chatEvent).getLiked());

                            }
                        }
                    });
                    messageViewHolder.materialCardView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("WhatsApp-Message", StringEscapeUtils.unescapeJava(((Message) chatEvent).getMsg()));
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(context, "Message copied...", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });

                    break;
                case TEXT_MESSAGE_SENT:
                    messageViewHolder.highFive.setVisibility(((Message) chatEvent).getLiked() ? View.VISIBLE : View.GONE);
                    messageViewHolder.sentMaterialcardview.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("WhatsApp-Message", StringEscapeUtils.unescapeJava(((Message) chatEvent).getMsg()));
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(context, "Message copied...", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    break;
            }



        }

    }

    private void setDate(DateViewHolder holder, Date sentAt) {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));

        try {
            //Date date=new Date(top.getcreated());

            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("IST"));
            Date currentdate = c.getTime();
            Date date = sentAt;

            Log.i("date", date.toString() + "   " + currentdate.toString());
            if (currentdate.getDate() == date.getDate()) {
                holder.date.setText(getToday(date));
            } else {
                holder.date.setText(getDate(date));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface HighFiveClickListener {
        void highFiveClick(String id, Boolean status);
    }

    public void setHighFiveListener(HighFiveClickListener highFiveListener) {
        this.highFiveClickListener = highFiveListener;
    }


    public String format(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH : mm");
        return simpleDateFormat.format(date);

    }

    @Override
    public int getItemCount() {
        return chatEvents.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatEvent chatEvent = chatEvents.get(position);
        if (chatEvent instanceof Message) {
            if (((Message) chatEvent).getSenderId().equals(currentUserId)) {
                return TEXT_MESSAGE_SENT;
            } else {
                return TEXT_MESSAGE_RECEIVED;
            }

        } else if (chatEvent instanceof DateHeader) {
            return DATE_HEADER;
        } else {
            return UNSUPPORTED;
        }

    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        TextView date;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.textView);
        }
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView content, time;
        ImageView highFive;
        MaterialCardView materialCardView,sentMaterialcardview;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            highFive = itemView.findViewById(R.id.highFiveImg);
            materialCardView = itemView.findViewById(R.id.messageCardView);
            sentMaterialcardview=itemView.findViewById(R.id.materialCardView3);
        }
    }

    abstract class DoubleClickListener implements View.OnClickListener {
        long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v);
                lastClickTime = 0;
            }
            lastClickTime = clickTime;
        }

        protected abstract void onDoubleClick(View v);

    }

    public String getDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM ");
        return simpleDateFormat.format(date);
    }

    public String getToday(Date date) {
        return "Today";
        //  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
        //return simpleDateFormat.format(date);
    }

    public String getTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
        return simpleDateFormat.format(date);
    }

}
